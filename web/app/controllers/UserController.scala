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
  private val cacheEmail2LogedInUser = mutable.Map.empty[String, User]

  private val cacheEmail2UserPass = mutable.Map.empty[String, (String, String)]

  def getUserFromToken(session: Session): Option[User] = {
    session.get(NAME).flatMap(name => session.get(TOKEN).flatMap(token => checkUserToken(name, token)))
  }


  def signup(request: Request[AnyContent]): Result = {
    val signUpDataOpt = ViewModel.read[SignUpData](request.body)
    if (signUpDataOpt.isEmpty)
      return JSONERROR

    val signUpData = signUpDataOpt.get
    if (cacheEmail2UserPass.get(signUpData.email).isDefined)
      return BadRequest("Email already exists")

    cacheEmail2UserPass.update(signUpData.email, (signUpData.username, signUpData.password))
    doLogin(LoginData(signUpData.email, signUpData.password), request.session)
  }

  //TODO decide between this and signup
  def signupFunctional(request: Request[AnyContent]): Result =
    ViewModel.read[SignUpData](request.body).map(signUpData =>
      cacheEmail2UserPass.get(signUpData.email).map(_ => BadRequest("Email already exists")).getOrElse {
        cacheEmail2UserPass.update(signUpData.email, (signUpData.username, signUpData.password))
        doLogin(LoginData(signUpData.email, signUpData.password), request.session)
      }).getOrElse(JSONERROR)


  def login(request: Request[AnyContent]): Result = {
    ViewModel.read[LoginData](request.body).map(loginData =>
      doLogin(loginData, request.session)
    ).getOrElse(JSONERROR)
  }

  private def doLogin(loginData: LoginData, session: Session): Result = {
    loginUser(loginData).map(user =>
      Ok.withSession(addUserToSession(session, user))
    ).getOrElse(BadRequest("Login Failed, Invalid Email-Password combination"))
  }

  private def loginUser(loginData: LoginData): Option[User] = {
    cacheEmail2UserPass.get(loginData.email).flatMap(userPass => {
      if (cacheEmail2LogedInUser.get(loginData.email).isDefined)
        None
      else {
        val user = User(userPass._1, generateToken())
        cacheEmail2LogedInUser.put(loginData.email, user)
        cacheToken2User.put(user.token, user)
        Some(user)
      }
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
    List("ysf", "nicolas", "dany", "ysf1", "nicolas1", "dany1", "ysf2", "nicolas2", "dany2", "ysf3", "nicolas3", "dany3")
  }

  private def generateToken(): String = {
    val builder = new mutable.StringBuilder(TOKEN_LENGTH)
    for (i <- 0 to TOKEN_LENGTH) {
      builder.append(Random.nextPrintableChar)
    }
    builder.toString
  }


}


