package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import akka.pattern.ask
import grizzled.slf4j.Logging
import tictactoe.actor.messages._
import tictactoe.actor.user.UserHandlerActor._
import tictactoe.actor.user.UserTokenManagerActor.{RequestUserHandlerForToken, TOKEN, UserHandlerIfPresent}
import tictactoe.model.User

import scala.collection.mutable
import scala.util.{Failure, Success}

/**
  */
class UserHandlerActor(user: User, token: TOKEN) extends Actor with Logging {

  private val socketCache: mutable.Set[ActorRef] = mutable.Set.empty[ActorRef]
  private val lobbyCache = mutable.Map.empty[TOKEN, UserElement]

  private val userManager = context.actorSelection(context.system / UserTokenManagerActor.NAME)
  private val lobby = context.actorSelection(context.system / LobbyActor.NAME)

  private var gameOpt: Option[ActorRef] = None
  private var opponentOpt: Option[ActorRef] = None
  private var moves: List[PlayerMove] = Nil

  override def preStart(): Unit = {
    lobby ! LobbyActor.RegisterUserToken(user.name, token)
    lobby ! LobbyActor.GetAll()
  }


  @scala.throws[Exception](classOf[Exception])
  override def postStop(): Unit = {
    lobby ! LobbyActor.UnRegisterUserToken(user.name, token)
  }

  def handleRegisterWebSocket(): Unit = {
    socketCache.add(sender())
    socketCache + sender()
  }

  def handleUnRegisterWebSocket(): Unit = {
    socketCache - sender()
    //FIXME IF EMPTY CLEAR
  }

  def handleRequestStatus(): Unit = {
    sender() ! UserStatus.toJson(UserStatus(user.name, token, lobbyCache.values.toList))
  }

  def handleRequestGameStatus(): Unit = {
    GameStatus.toJson(GameStatus(moves))
  }

  def handleGetAllReturn(list: List[UserElement]): Unit = {
    list.foreach(e => lobbyCache.put(e.token, e))
  }

  def handleBroadcastRegisterUserToken(token: String, username: String): Unit = {
    if (token == this.token)
      return
    val elem = UserElement(username, token)
    lobbyCache.put(token, elem)
    if (gameOpt.isDefined)
      broadcast(UserLoggedIn.toJson(elem))
  }

  def handleBroadcastUnRegisterUserToken(token: String, username: String): Unit = {
    if (token == this.token)
      return
    val elem = UserElement(username, token)
    lobbyCache.remove(token)
    if (gameOpt.isDefined)
      broadcast(UserLoggedOut.toJson(elem))
  }

  def handleAskOtherPlayerForGame(user: UserElement): Unit = {
    (userManager ? RequestUserHandlerForToken).mapTo[UserHandlerIfPresent]
      .onComplete {
        case Success(msg) =>
          msg.handlerOpt.foreach(value => {
            self ! AskOtherPlayerForGameFORWARD(user, value.handler)
          })
        case Failure(_) =>
      }
  }

  def handleAskOtherPlayerForGame(user: UserElement, userActor: ActorRef): Unit = {
    userActor ! BeingAskedForGame(UserElement(user.name, token), self)
  }

  private val beingAskedBy = mutable.Map.empty[TOKEN, ActorRef]

  def handleBeingAskedForGame(otherPlayer: UserElement, otherRef: ActorRef): Unit = {
    if (gameOpt.isDefined)
      otherRef ! AcceptOrDenyGameWithRef(UserElement(user.name, token), accept = false, self)
    else {
      beingAskedBy.put(otherPlayer.token, otherRef)
      broadcast(GameRequested.toJson(otherPlayer))
    }
  }

  def handleAcceptOrDenyGame(otherPlayer: UserElement, accept: Boolean): Unit = {
    beingAskedBy.remove(otherPlayer.token) match {
      case None => //ignore
      case Some(other) =>
        val msg = AcceptOrDenyGameWithRef(UserElement(user.name, token), accept, self)
        other ! msg
        if (accept) {
          val bad = msg.copy(accept = false)
          beingAskedBy.values.foreach(_ ! bad)
          beingAskedBy.clear()
        }
    }
  }

  def handleAcceptOrDenyGameWithRef(otherPlayer: UserElement, accept: Boolean, otherRef: ActorRef): Unit = {
    broadcast(AskForGame.toJson(AcceptGame(otherPlayer.name, otherPlayer.token, accept)))
    //TODO if accept startGame
  }


  def handleSendDirectMessage(directMessage: DirectMessage): Unit = {
    opponentOpt match {
      case None => //ignore
      case Some(ref) => ref ! BroadCastDirectMessage(directMessage)
    }

  }

  def handleBroadCastDirectMessage(directMessage: DirectMessage): Unit = {
    broadcast(directMessage)
  }

  override def receive: Receive = {
    case RegisterWebSocket() => handleRegisterWebSocket()
    case UnRegisterWebSocket() => handleUnRegisterWebSocket()
    case RequestStatus() => handleRequestStatus()
    case RequestGameStatus() => handleRequestGameStatus()

    case LobbyActor.GetAllReturn(list: List[UserElement]) =>
      handleGetAllReturn(list: List[UserElement])
    case LobbyActor.BroadcastRegisterUserToken(token: String, username: String) =>
      handleBroadcastRegisterUserToken(token: String, username: String)
    case LobbyActor.BroadcastUnRegisterUserToken(token: String, username: String) =>
      handleBroadcastUnRegisterUserToken(token: String, username: String)

    case AskOtherPlayerForGame(user: UserElement) =>
      handleAskOtherPlayerForGame(user: UserElement)
    case AskOtherPlayerForGameFORWARD(user: UserElement, userActor: ActorRef) =>
      handleAskOtherPlayerForGame(user: UserElement, userActor: ActorRef)
    case BeingAskedForGame(otherPlayer: UserElement, otherRef: ActorRef) => handleBeingAskedForGame(otherPlayer: UserElement, otherRef: ActorRef)
    case AcceptOrDenyGame(otherPlayer: UserElement, accept: Boolean) =>
      handleAcceptOrDenyGame(otherPlayer: UserElement, accept: Boolean)
    case AcceptOrDenyGameWithRef(otherPlayer: UserElement, accept: Boolean, otherRef: ActorRef) =>
      handleAcceptOrDenyGameWithRef(otherPlayer: UserElement, accept: Boolean, otherRef: ActorRef)

    case SendDirectMessage(directMessage: DirectMessage) =>
      handleSendDirectMessage(directMessage: DirectMessage)
    case BroadCastDirectMessage(directMessage: DirectMessage) =>
      handleBroadCastDirectMessage(directMessage: DirectMessage)

    case any => warn(s"illegal message + $any")
      throw new IllegalArgumentException("Invalid message")
  }

  def broadcast(message: Any): Unit = {
    socketCache.foreach(_ ! message)
  }

}


object UserHandlerActor {

  case class RequestGameStatus()

  case class RequestGameStatusRet(either: Either[String, IllegalStateException])

  case class RequestStatus()

  case class RequestStatusRet(either: Either[String, IllegalStateException])

  case class BroadcastMessage(message: String)

  case class RegisterWebSocket()

  case class UnRegisterWebSocket()

  case class AskOtherPlayerForGame(user: UserElement)

  private case class AskOtherPlayerForGameFORWARD(user: UserElement, userActor: ActorRef)

  private case class BeingAskedForGame(otherPlayer: UserElement, otherRef: ActorRef)

  case class AcceptOrDenyGame(otherPlayer: UserElement, accept: Boolean)

  case class AcceptOrDenyGameWithRef(otherPlayer: UserElement, accept: Boolean, otherRef: ActorRef)

  case class SendDirectMessage(directMessage: DirectMessage)

  case class BroadCastDirectMessage(directMessage: DirectMessage)


  def props(user: User, token: String): Props = Props(new UserHandlerActor(user: User, token: String))
}