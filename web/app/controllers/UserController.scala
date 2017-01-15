package controllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import grizzled.slf4j.Logging
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result, Session}
import silhouette.TicTacToeEnv
import tictactoe.TicTacToeServer
import tictactoe.exceptions.PersistenceException._
import tictactoe.exceptions.ShouldBeInjectedException
import tictactoe.model.User
import tictactoe.model.entity.UserId
import tictactoe.persistence.entityManagement.mutator.Wrapper
import viewModel.{LoginData, SignUpData, ViewModel}

import scala.collection.mutable
import scala.util.Random

/**
  */
object UserController extends Logging {
  private val TOKEN_LENGTH = 32
  val JSONERROR = BadRequest("JSON not as expected")

  val TOKEN = "token"
  val NAME = "name"

  private val cacheToken2User = mutable.Map.empty[String, User]

  //only if logged in
  private val cacheEmail2LoggedInUser = mutable.Map.empty[String, User]

  private val cacheEmail2UserPass = mutable.Map.empty[String, (String, String)]

  def getUserFromToken(session: Session): Option[User] = {
    session.get(NAME).flatMap(name => session.get(TOKEN).flatMap(token => checkUserToken(name, token)))
  }

  def getUserFromEmail(email: String): Option[User] = {
    cacheEmail2LoggedInUser.get(email)
  }

  def getUsers(user: User, request: Request[AnyContent]): Result = {
    //TODO: get users names.
    // array in the form  [{username: this user name},{users: all users }]
    Ok("")
  }

  def signUp(request: Request[AnyContent]): Result =
    ViewModel.read[SignUpData](request.body) match {
      case None => JSONERROR
      case Some(signUpData) =>
        cacheEmail2UserPass.get(signUpData.email) match {
          case Some(_) =>
            BadRequest("Email already exists")
          case None =>
            cacheEmail2UserPass.update(signUpData.email, (signUpData.name, signUpData.password))
            doLogin(LoginData(signUpData.email, signUpData.password, signUpData.rememberMe), request.session)
        }
    }

  def logout(request: SecuredRequest[TicTacToeEnv, AnyContent]): Result = {
    val user = request.identity
    cacheToken2User.get(user.token).filter(_.name == user.name)
      .map(usr => cacheEmail2LoggedInUser.remove(usr.email)) match {
      case None => BadRequest("Not logged in")
      case Some(_) => Redirect(routes.Application.signUpPage()).withNewSession
    }
  }


  def login(request: Request[AnyContent]): Result =
    ViewModel.read[LoginData](request.body) match {
      case None => JSONERROR
      case Some(loginData) => doLogin(loginData, request.session)
    }


  private def doLogin(loginData: LoginData, session: Session): Result = {
    if (cacheEmail2LoggedInUser.get(loginData.email).isDefined)
      return BadRequest("User already logged in")

    loginUser(loginData) match {
      case None => BadRequest("Login Failed, Invalid Email-Password combination")
      case Some(user) => Ok("").withSession(addUserToSession(session, user))
    }
  }

  private def unPair[A, B, R](f: (A, B) => R): ((A, B)) => R = {
    (pair) => f(pair._1, pair._2)
  }

  private def loginUser(loginData: LoginData): Option[User] = {
    cacheEmail2UserPass.get(loginData.email).filter(pair => {
      val (_, pw) = pair
      pw == loginData.password
    })

    cacheEmail2UserPass.get(loginData.email).filter(unPair((_, pw) => pw == loginData.password)).map(userPass => {
      val user = User(UserId(), userPass._1, generateToken(), loginData.email)
      cacheEmail2LoggedInUser.put(loginData.email, user)
      cacheToken2User.put(user.token, user)
      user
    })
  }

  private def addUserToSession(session: Session, usr: User): Session = {
    val data = session.data.updated(NAME, usr.name).updated(TOKEN, usr.token)
    session.copy(data = data)
  }


  private def checkUserToken(username: String, token: String): Option[User] = {
    cacheToken2User.get(token).filter(usr => usr.name == username)
  }


  def getAllActiveUserNames: List[String] = {
    cacheEmail2LoggedInUser.values.toList.map(_.name)
  }

  private def generateToken(): String = {
    val builder = new mutable.StringBuilder(TOKEN_LENGTH)
    for (i <- 0 to TOKEN_LENGTH) {
      builder.append(Random.nextPrintableChar)
    }
    builder.toString
  }

  //==============================================================================================

  @Inject
  lazy val server: TicTacToeServer = throw ShouldBeInjectedException()


  /** Register and login a player.
    *
    * @param email e-mail
    * @param name  name
    * @return controllers.authentication of the player */
  @throws[IllegalNameException]
  @throws[IllegalEmailException]
  @throws[EmailInUseException]
  @throws[IllegalPasswordException]
  @throws[PasswordsDoNotMatchException]
  def register(email: String,
               name: String): User = {
    info(s"register(email=$email, name=$name, password=***, passwordRepetition=***)")
    val usr = User(email = email, name = name)
    server.persistence.userManager.add(usr)
    usr
  }


  def confirmEmail(email: String): User = {
    info(s"confirmEmail(email=$email")
    val targetId = server.persistence.authenticationManager.getPlayerIdByEmail(email)
    server.persistence.userManager.directUpdate(targetId, _.copy(emailConfirmed = true))
    get(targetId).get
  }

  def get(id: UserId): Wrapper[User] = {
    info(s"getPlayer(id=$id)")
    server.persistence.userManager.get(id).wrapper
  }


  /** Get a player by email
    *
    * @param email email of the user
    * @return Player
    */
  @throws[EntityNotFoundException]("Player not found")
  def getUserByEmail(email: String): Wrapper[User] = {
    val playerId = server.persistence.authenticationManager.getPlayerIdByEmail(email)
    get(playerId)
  }


  def modifyPasswordHash(userId: UserId,
                         passwordHash: String): Unit = {
    info(s"modifyPasswordHash(auth.id=$userId, passwordHash=***)")
    server.persistence.authenticationManager.addPasswordHash(userId, passwordHash)
  }

  def getPassword(userId: UserId): String = {
    info(s"getPassword(playerId=$userId")
    server.persistence.authenticationManager.readPasswordHash(userId).get
  }

}


