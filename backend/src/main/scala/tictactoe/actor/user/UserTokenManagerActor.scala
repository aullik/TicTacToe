package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import grizzled.slf4j.Logging
import tictactoe.actor.user.UserTokenManagerActor._
import tictactoe.model.User
import util.TokenGenerator

import scala.collection.mutable

/**
  */
class UserTokenManagerActor extends Actor with Logging {

  private val tokenGen = new TokenGenerator()
  private val tokenCache = mutable.HashMap.empty[TOKEN, UserHandlerContainer]

  private val userCache = mutable.HashMap.empty[EMAIL, UserHandlerContainer]


  override def receive: Receive = {
    case RequestUserHandlerForEmail(email: EMAIL) => handleRequestUserHandlerForEmail(email: EMAIL)
    case RequestUserHandlerForToken(token: TOKEN) => handleRequestUserHandlerForToken(token: TOKEN)
    case RegisterForUser(user: User) => handleRegisterForUser(user: User)

    case any => warn(s"illegal message + $any")
      throw new IllegalArgumentException("Invalid message")
  }

  def handleRequestUserHandlerForEmail(email: EMAIL): Unit = {
    sender ! UserHandlerIfPresent(userCache.get(email))
  }

  def handleRequestUserHandlerForToken(token: TOKEN): Unit = {
    sender ! UserHandlerIfPresent(userCache.get(token))
  }

  def handleRegisterForUser(user: User): Unit = {
    val email = user.email
    val ret =
      userCache.get(email) match {
        case None =>
          val token = tokenGen.generateToken(user.name)
          val cont = UserHandlerContainer(
            context.actorOf(UserHandlerActor.props(user, token)), user, token)
          tokenCache.put(token, cont)
          userCache.put(email, cont)
          cont
        case Some(cont) => cont
      }

    val s = sender()
    ret.handler.!(UserHandlerActor.RegisterWebSocket())(s)
    s ! UserHandlerIfPresent(Some(ret))
  }

}

case class UserHandlerContainer(handler: ActorRef, user: User, token: TOKEN)


object UserTokenManagerActor {

  final val NAME = "UserTokenManagerActor"

  private[user] final type TOKEN = String
  private final type EMAIL = String

  case class RegisterForUser(user: User)

  case class RequestUserHandlerForEmail(email: EMAIL)

  case class RequestUserHandlerForToken(token: TOKEN)

  case class UserHandlerIfPresent(handlerOpt: Option[UserHandlerContainer])


  def props: Props = Props(new UserTokenManagerActor())
}