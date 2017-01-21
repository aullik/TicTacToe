package tictactoe.controllers.webControllers

import javax.inject.Inject

import tictactoe.silhouette.{MailTokenService, TicTacToeEnv, UserService}
import com.google.inject.{Provider, Singleton}
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials, PasswordHasherRegistry}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.routes
import tictactoe.mailer.{MailService, MailTokenUser, Mailer}
import net.ceedubs.ficus.Ficus._
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization._
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Request, Result}
import tictactoe.silhouette.Implicits._
import tictactoe.viewModel.{LoginData, SignUpData}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

/** Authentication Controller for the Application
  *
  * @param silhouette             Silhouette Stack which provides all the Silhouette actions
  * @param messagesApi            The internationalisation API
  * @param playerService          The playerService to interact with the silhouette Identity
  * @param authInfoRepository     Contains tictactoe.authentication Infos
  * @param tokenService           MailTokenService which contains functions to interact with the MailTokens of players
  * @param passwordHasherRegistry A password hasher
  * @param credentialsProvider    A provider for authenticating with credentials
  * @param mailService            A MailService to send emails
  * @param conf                   Play Configuration
  * @param clock                  Clock implementation*
  */
@Singleton
class Auth @Inject private(val silhouette: Silhouette[TicTacToeEnv],
                           val messagesApi: MessagesApi,
                           val webProvider: Provider[WebControllerContainer],
                           playerService: UserService,
                           authInfoRepository: AuthInfoRepository,
                           tokenService: MailTokenService[MailTokenUser],
                           passwordHasherRegistry: PasswordHasherRegistry,
                           credentialsProvider: CredentialsProvider,
                           mailService: MailService,
                           conf: Configuration,
                           clock: Clock
                          ) extends AuthController with WebController {


  implicit val formats = Serialization.formats(NoTypeHints)

  implicit val ms: MailService = mailService


  /** Starts the sign up procedure
    *
    * Sends a mail to new player with a link to confirm email address
    *
    * @param request  user request
    * @param messages messages for preferred language
    * @return BadRequest if the user already exists or Ok
    */
  def startSignUp(request: Request[AnyContent], messages: Messages): Future[Result] = {
    info("handleStartSignUp")
    val json = request.body.asJson.get
    val signUpData = json.validate[SignUpData].get

    val loginInfo: LoginInfo = key2loginInfo(signUpData.email)
    playerService.retrieve(loginInfo).flatMap {
      case Some(_) => Future.successful(BadRequest(write("error" -> Messages("welcome.signUp.user.notUnique"))))
      case None =>
        val token = MailTokenUser(signUpData.email, isSignUp = true)
        for {
          savedUser <- Future.successful(UserController.register(signUpData.email, signUpData.name))
          _ <- authInfoRepository.add(loginInfo, passwordHasherRegistry.current.hash(signUpData.password))
          _ <- tokenService.create(token)
        } yield {
          Mailer.welcome(savedUser, link = routes.ScalaRoutes.signUpEmail(token.id).absoluteURL()(request))(ms, messages)
          Ok("Registered")
        }
    }
  }

  /** Finishes the sign up procedure
    *
    * @param tokenId the id of the MailToken
    * @param request the user request
    * @return redirects to index if sign up was successful else a BadRequest
    */
  def signUpEmail(tokenId: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
    info("signUp")
    tokenService.retrieve(tokenId).flatMap {
      case Some(token) if token.isSignUp && !token.isExpired =>
        playerService.retrieve(key2loginInfo(token.email)).flatMap {
          case Some(player) =>
            env.authenticatorService.create(key2loginInfo(player.email))(request).flatMap { authenticator =>
              if (!player.emailConfirmed) {
                Future.successful(UserController.confirmEmail(player.email)).map(newPlayer =>
                  env.eventBus.publish(SignUpEvent(newPlayer, request))
                )
              }
              for {
                cookie <- env.authenticatorService.init(authenticator)(request)
                result <- env.authenticatorService.embed(cookie, Redirect(routes.ScalaRoutes.index()))(request)
              } yield {
                tokenService.consume(tokenId)
                result
              }
            }
          case None => Future.successful(NotFound(views.html.errors.registration()(messages)))
        }
      case Some(token) =>
        tokenService.consume(tokenId)
        Future.successful(NotFound(views.html.errors.registration()(messages)))
      case None => Future.successful(NotFound(views.html.errors.registration()(messages)))
    }
  }

  /** Authenticates a player
    *
    * @param request user request
    * @return a result with additional authenticator info or a BadRequest
    */
  def authenticate(request: Request[AnyContent]): Future[Result] = {
    info("authenticate")
    val json = request.body.asJson.get
    json.validate[LoginData].fold(
      formWithErrors => Future.successful(BadRequest("Illegal json")),
      formData => {
        val LoginData(email, password, rememberMe) = formData
        credentialsProvider.authenticate(Credentials(email, password)).flatMap { loginInfo =>
          playerService.retrieve(loginInfo).flatMap {
            case Some(user) => for {
              authenticator <- env.authenticatorService.create(loginInfo)(request).map(authenticatorWithRememberMe(_, rememberMe))
              cookie <- env.authenticatorService.init(authenticator)(request)
              result <- env.authenticatorService.embed(cookie, Redirect(routes.ScalaRoutes.index()))(request)
            } yield {
              env.eventBus.publish(LoginEvent(user, request))
              result
            }
            case None => Future.successful(BadRequest("Couldn't find user"))
          }
        }.recover {
          case e: ProviderException => BadRequest("auth.credentials.incorrect")
        }
      }
    )
  }

  /** Initializes a authenticator
    *
    * @param authenticator new CookieAuthenticator
    * @param rememberMe    true if user should be remembered
    * @return authenticator with rememberMe or without
    */
  private def authenticatorWithRememberMe(authenticator: CookieAuthenticator, rememberMe: Boolean) = {
    if (rememberMe) {
      authenticator.copy(
        expirationDateTime = clock.now.plusDays(rememberMeParams._1),
        idleTimeout = rememberMeParams._2,
        cookieMaxAge = rememberMeParams._3
      )
    } else {
      authenticator
    }
  }

  /** Parameters for authenticator (from config)
    *
    */
  private lazy val rememberMeParams: (Int, Option[FiniteDuration], Option[FiniteDuration]) = {
    val cfg = conf.getConfig("tictactoe.silhouette.authenticator.rememberMe").get.underlying
    val ficusCFG = toFicusConfig(cfg)
    (
      cfg.getInt("authenticatorExpiryInDays"),
      ficusCFG.getAs[FiniteDuration]("authenticatorIdleTimeout"),
      ficusCFG.getAs[FiniteDuration]("cookieMaxAge")
    )
  }

  /** Signs a user out and deletes cookie
    *
    * @param request user request
    * @return redirects user to welcome page
    */
  def signOut(request: SecuredRequest[TicTacToeEnv, AnyContent]): Future[Result] = {
    info("signOut")
    env.eventBus.publish(LogoutEvent(request.identity, request))
    env.authenticatorService.discard(request.authenticator, Redirect(routes.ScalaRoutes.signUpPage()))(request)
  }

  //  /** Starts the reset password procedure
  //    *
  //    * Sends a mail to the player with a link to reset password
  //    *
  //    * @return Result
  //    */
  //  def resetPasswordRequest(request: Request[AnyContent]): Future[Result] = {
  //    info("resetPasswordRequest")
  //    val json = request.body.asJson.get
  //    json.validate[ResetPasswordRequest].fold(
  //      formWithErrors => Future.successful(BadRequest("Illegal json")),
  //      resetData => playerService.retrieve(resetData.email).flatMap {
  //        case Some(_) =>
  //          val token = MailTokenUser(resetData.email, isSignUp = false)
  //          tokenService.create(token).map { _ =>
  //            Mailer.forgotPassword(resetData.email, link = routes.ScalaRoutes.startPasswordReset(token.id).url)
  //            Ok("Reset password started")
  //          }
  //        case None => Future.successful(BadRequest("Couldn't find user"))
  //      }
  //    )
  //  }
  //
  //  /** Redirects to password reset page
  //    *
  //    * @param tokenId  token of password reset request
  //    * @param request  user request
  //    * @param messages for preferred language
  //    * @return Result
  //    */
  //  def startPasswordReset(tokenId: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
  //    info("startPasswordReset")
  //    tokenService.retrieve(tokenId).flatMap {
  //      case Some(token) if !token.isSignUp && !token.isExpired => Future.successful(Redirect(routes.ScalaRoutes.resetPasswordForm(tokenId)))
  //      case Some(token) =>
  //        tokenService.consume(tokenId)
  //        Future.successful(BadRequest("error")) //TODO Make View
  //      case None => Future.successful(BadRequest("error")) //TODO Make View
  //    }
  //  }
  //
  //  def resetPassword(tokenId: String)(request: Request[AnyContent], messages: Messages): Future[Result] = {
  //    info("resetPassword")
  //    val json = request.body.asJson.get
  //    json.validate[ResetPassword].fold(
  //      formWithErrors => Future.successful(BadRequest("Illegal json")),
  //      resetData => {
  //        tokenService.retrieve(tokenId).flatMap {
  //          case Some(token) if !token.isSignUp && !token.isExpired =>
  //            val loginInfo: LoginInfo = token.email
  //            playerService.retrieve(loginInfo).flatMap {
  //              case Some(user) =>
  //                for {
  //                  _ <- authInfoRepository.update(loginInfo, passwordHasherRegistry.current.hash(resetData.password))
  //                  authenticator <- env.authenticatorService.create(user.email)(request)
  //                  result <- env.authenticatorService.renew(authenticator, Ok("Changed Password"))(request)
  //                } yield {
  //                  tokenService.consume(tokenId)
  //                  env.eventBus.publish(LoginEvent(user, request))
  //                  result
  //                }
  //              case None => Future.successful(BadRequest("Couldn't find user"))
  //            }
  //          case Some(token) =>
  //            tokenService.consume(tokenId)
  //            Future.successful(BadRequest("error")) //TODO Make View
  //          case None => Future.successful(BadRequest("error")) //TODO Make View
  //        }
  //      })
  //  }
}
