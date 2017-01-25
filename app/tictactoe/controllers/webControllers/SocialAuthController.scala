package tictactoe.controllers.webControllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.impl.providers._
import controllers.{WebJarAssets, routes}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.{AnyContent, Controller, Request, Result}
import tictactoe.silhouette.{TicTacToeEnv, UserService}

import scala.concurrent.Future

/**
  * The social auth controller.
  *
  * @param messagesApi            The Play messages API.
  * @param silhouette             The Silhouette stack.
  * @param userService            The user service implementation.
  * @param authInfoRepository     The auth info service implementation.
  * @param socialProviderRegistry The social provider registry.
  * @param webJarAssets           The webjar assets implementation.
  */
class SocialAuthController @Inject()(
                                      val messagesApi: MessagesApi,
                                      silhouette: Silhouette[TicTacToeEnv],
                                      userService: UserService,
                                      authInfoRepository: AuthInfoRepository,
                                      socialProviderRegistry: SocialProviderRegistry,
                                      implicit val webJarAssets: WebJarAssets,
                                      userController: UserController)
  extends Controller with I18nSupport with Logger {

  /**
    * Authenticates a user against a social provider.
    *
    * @param provider The ID of the provider to authenticate against.
    * @return The result to display.
    */
  def authenticate(provider: String)(request: Request[AnyContent]): Future[Result] = {
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate()(request).flatMap {
          case Left(result) => Future.successful(result)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            //            user <- userService.save(profile)
            user <- Future.successful(userController.saveOauth(profile))
            authInfo <- authInfoRepository.save(profile.loginInfo, authInfo)
            authenticator <- silhouette.env.authenticatorService.create(profile.loginInfo)(request)
            value <- silhouette.env.authenticatorService.init(authenticator)(request)
            result <- silhouette.env.authenticatorService.embed(value, Ok("logged In"))(request)
          } yield {
            silhouette.env.eventBus.publish(LoginEvent(user, request))
            Redirect(routes.ScalaRoutes.index()).withSession(result.session(request))
          }
        }
      case _ => Future.failed(new ProviderException(s"Cannot authenticate with unexpected social provider $provider"))
    }).recover {
      case e: ProviderException =>
        logger.error("Unexpected provider error", e)
        BadRequest("auth.credentials.incorrect")
    }
  }
}
