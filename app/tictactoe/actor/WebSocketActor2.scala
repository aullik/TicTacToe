package tictactoe.actor

import akka.actor.{Actor, ActorRef, Props}
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import tictactoe.actor.messages._
import tictactoe.actor.user.UserHandlerActor.BroadcastMessage
import tictactoe.actor.user.{UserHandlerActor, UserHandlerContainer, UserTokenManagerActor}
import tictactoe.model.User

/**
  */
class WebSocketActor2(out: ActorRef, user: User) extends Actor with Logging {

  private val userManager = context.actorSelection(context.system / UserTokenManagerActor.NAME)

  private var userHandler: Option[UserHandlerContainer] = None

  override def preStart(): Unit = {
    userManager ! UserTokenManagerActor.RegisterForUser(user)
  }

  override def postStop(): Unit = {
    userHandler.foreach(_.handler ! UserHandlerActor.UnRegisterWebSocket())
  }

  object ExtendedFunction extends PartialFunction[Any, Unit] {


    private val pf: PartialFunction[Any, Unit] = {
      case msg: String => handleMsg(msg)
      case BroadcastMessage(msg) => handleBroadcastMessage(msg)
      case UserTokenManagerActor.UserHandlerIfPresent(opt) => setUserHandler(opt)

      case any => error(s"Invalid message: + $any")
        throw new IllegalArgumentException(s"Invalid message: + $any")
    }

    override def isDefinedAt(x: Any): Boolean = pf.isDefinedAt(x)

    override def apply(v1: Any): Unit = {
      if (userHandler.isDefined || v1.isInstanceOf[UserTokenManagerActor.UserHandlerIfPresent]) {
        pf(v1)
      } else {
        //send to self -> move to the back of the inbox. UserTokenManagerActor.UserHandlerIfPresent is needed to set handler
        self.!(v1)(sender())
      }
    }
  }

  private def setUserHandler(opt: Option[UserHandlerContainer]) = {
    opt match {
      case None => throw new IllegalArgumentException("userHandler must be set!")
      case some => userHandler = some
    }
  }

  def handleBroadcastMessage(msg: String): Unit = {
    out ! msg
  }


  override def receive: Receive = ExtendedFunction


  def handleMsg(msg: String): Unit = {
    try Json.parse(msg) match {
      case UserStatus(_) => handleUserStatus()
      case AskForGame(value) => handleAskForGame(value)
      case GameRequested(value) => handleGameRequested(value)
      case GameStatus(_) => handleGameStatus()
      case GamePlayers(_) => handleGamePlayers()
      case Move(value) => handleMove(value)
      case DirectMessage(value) => handleMessage(value)
      case any => throw new IllegalArgumentException(s"Invalid message: + $any")
    } catch {
      case e: Exception =>
        warn(s"couldn't handle message: $msg. Exception: $e")
    }
  }


  def handleUserStatus() = ???

  def handleAskForGame(value: UserElement) = ???

  def handleGameRequested(value: AcceptGame) = ???

  def handleGameStatus() = ???

  def handleGamePlayers() = ???

  def handleMove(value: Move) = ???

  def handleMessage(value: DirectMessage) = ???


}

object WebSocketActor2 {

  def apply(out: ActorRef, usr: User): Props = {
    Props(new WebSocketActor2(out, usr))

  }
}
