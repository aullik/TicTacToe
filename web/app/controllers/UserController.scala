package controllers

import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result, Session}
import viewModel.{LoginData, SignUpData, ViewModel}

import scala.collection.mutable
import scala.util.Random

/**
  */
object UserController {
  private val TOKEN_LENGTH = 32
  val JSONERROR = BadRequest("JSON did not as expected")

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
            cacheEmail2UserPass.update(signUpData.email, (signUpData.username, signUpData.password))
            doLogin(LoginData(signUpData.email, signUpData.password, signUpData.rememberMe), request.session)
        }
    }

  def logout(user: User, request: Request[AnyContent]): Result =
    cacheToken2User.get(user.token).filter(_.name == user.name)
      .map(usr => cacheEmail2LoggedInUser.remove(usr.email)) match {
      case None => BadRequest("Not logged in")
      case Some(_) => Redirect(routes.Application.signUpPage())
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
      case Some(user) =>
        Redirect(routes.Application.index()).withSession(addUserToSession(session, user))
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
      val user = User(userPass._1, generateToken(), loginData.email)
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


}


