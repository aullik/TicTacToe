package controllers

import play.api.mvc.Session

import scala.collection.mutable
import scala.util.Random

/**
  */
object UserController {

  private val tokenCache = Map.empty[User, String]

  def getUserFromToken(session: Session): Option[User] = {
    None
  }

  def login(session: Session): Session = {
    println("login: \n" + session.data)

    session
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


