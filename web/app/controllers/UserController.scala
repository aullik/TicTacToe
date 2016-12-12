package controllers

import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result, Session}
import viewModel.{LoginData, ViewModel}

import scala.collection.mutable
import scala.util.Random

/**
  */
object UserController {

  private val cacheToken2User = Map.empty[String, User]
  private val cacheUserName2Token = Map.empty[String, String]

  def getUserFromToken(session: Session): Option[User] = {
    session.get("name").flatMap(name => session.get("token").flatMap(token => checkUserToken(name, token)))
  }


  def signup(request: Request[AnyContent]): Result = {
    println("data: " + request.body.asJson.get)
    println("signup: \n" + request.session.data)
    request.session.data.foreach(println _)

    //TODO: save user in the users list and create an new user object
    Ok("ok")
  }

  def login(request: Request[AnyContent]): Result = {
    println("data: " + ViewModel.read[LoginData](request.body))
    //TODO Youssef => form should fail, json should work
    //    val json = request.body.asJson.get
    //    println(json)
    //val form = request.body.asFormUrlEncoded.get
    val changedSession = request.session

    //TODO: check if the data is from a current user if true go to the index page else return with errors.
    Ok.withSession(changedSession)
  }


  private def checkUserToken(username: String, token: String): Option[User] = {
    cacheToken2User.get(token).filter(usr => usr.name == username)
  }


  private val TOKEN_LENGTH = 32


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


