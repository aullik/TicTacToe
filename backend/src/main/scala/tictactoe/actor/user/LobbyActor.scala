package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import grizzled.slf4j.Logging
import tictactoe.actor.messages.UserElement
import tictactoe.actor.user.LobbyActor._
import util.FunctionalHelper.ofTuple

import scala.collection.mutable

/**
  */
class LobbyActor extends Actor with Logging {

  private val cache = mutable.Map.empty[String, UserTokenContainer]

  def handleRegisterUserToken(token: String, username: String): Unit = {
    cache.put(token, new UserTokenContainer(token, username, sender()))
    broadCastMessage(BroadcastRegisterUserToken(token, username))
  }

  def handleUnRegisterUserToken(token: String, username: String): Unit = {
    cache.remove(token)
    broadCastMessage(BroadcastUnRegisterUserToken(token, username))
  }

  def broadCastMessage(message: Any): Unit = {
    cache.foreach(_._2.ref ! message)
  }

  def handleGetAll(): Unit = {
    val list = cache.toStream.map(ofTuple((_, cont) => UserElement(cont.username, cont.token))).toList
    sender() ! GetAllReturn(list)
  }

  def handleSetUserTokenInGame(token: String, inGame: Boolean): Unit = {
    cache.get(token) match {
      case None => //ignore
      case Some(usr) =>
        usr.ingame = inGame
        if (inGame) broadCastMessage(BroadcastUnRegisterUserToken(token, usr.username))
        else broadCastMessage(BroadcastRegisterUserToken(token, usr.username))

    }

  }

  def handleAskPlayerInGame(token: String): Unit = {
    sender() ! ReturnPlayerInGame(cache.get(token).exists(_.ingame))
  }

  override def receive: Receive = {
    case RegisterUserToken(token: String, username: String) => handleRegisterUserToken(token: String, username: String)
    case UnRegisterUserToken(token: String, username: String) => handleUnRegisterUserToken(token: String, username: String)
    case GetAll() => handleGetAll()
    case SetUserTokenInGame(token: String, inGame: Boolean) => handleSetUserTokenInGame(token: String, inGame: Boolean)
    case AskPlayerInGame(token: String) => handleAskPlayerInGame(token: String)


    case any => warn(s"illegal message + $any")
      throw new IllegalArgumentException("Invalid message")
  }
}

private class UserTokenContainer(val token: String, val username: String, val ref: ActorRef, var ingame: Boolean = false)


object LobbyActor {

  final val NAME = "LobbyActor"

  case class RegisterUserToken(token: String, username: String)

  case class BroadcastRegisterUserToken(token: String, username: String)

  case class UnRegisterUserToken(token: String, username: String)

  case class BroadcastUnRegisterUserToken(token: String, username: String)

  case class SetUserTokenInGame(token: String, inGame: Boolean)

  case class GetAll()

  case class GetAllReturn(list: List[UserElement])

  case class AskPlayerInGame(token: String)

  case class ReturnPlayerInGame(inGame: Boolean)


  def props: Props = Props(new LobbyActor())
}
