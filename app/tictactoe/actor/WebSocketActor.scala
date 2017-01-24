package tictactoe.actor

import akka.actor.{Actor, ActorRef, Props}
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import tictactoe.actor.messages.WebSocketActorMessages.JsType
import tictactoe.actor.messages._
import tictactoe.actor.user.UserHandlerActor.BroadcastMessage
import tictactoe.actor.user.{UserHandlerActor, UserHandlerContainer, UserTokenManagerActor}
import tictactoe.model.User

/**
  */
class WebSocketActor(out: ActorRef, user: User) extends Actor with Logging {

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
    info(msg)
    out ! msg
  }


  override def receive: Receive = ExtendedFunction

  object WithUserHandler {
    def apply(block: (UserHandlerContainer) => Unit): Unit = {
      userHandler match {
        case None => throw new IllegalStateException("no UserHandler")
        case Some(cont) => block(cont)
      }
    }
  }


  def handleMsg(msg: String): Unit = {
    info(msg)
    try {
      val json: JsType = Json.parse(msg)
      json match {
        case UserStatusMSG(_) => WithUserHandler(handleUserStatus())
        case AskForGame(value) => WithUserHandler(handleAskForGame(value))
        case GameRequested(value) => WithUserHandler(handleGameRequested(value))
        case GameStatus(_) => WithUserHandler(handleGameStatus())
        case GamePlayers(_) => WithUserHandler(handleGamePlayers())
        case Move(value) => WithUserHandler(handleMove(value))
        case DirectMessage(value) => WithUserHandler(handleMessage(value))
        case any => throw new IllegalArgumentException(s"Invalid message: + $any")
      }
    } catch {
      case e: Exception =>
        warn(s"couldn't handle message: $msg. Exception: ", e)
    }
  }


  def handleUserStatus()(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.RequestStatus()
  }

  def handleAskForGame(value: UserElement)(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.AskOtherPlayerForGame(value)
  }

  def handleGameRequested(value: AcceptGame)(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.AcceptOrDenyGame(tictactoe.actor.messages.UserElement(value.name, value.token), accept = value.accept)
  }

  def handleGameStatus()(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.AskGameStatus()
  }

  def handleGamePlayers()(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.AskGamePlayers()
  }

  def handleMove(value: Move)(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.GameMove(value)
  }

  def handleMessage(value: DirectMessage)(cont: UserHandlerContainer): Unit = {
    cont.handler ! UserHandlerActor.SendDirectMessage(value)
  }

}

object WebSocketActor {

  def apply(out: ActorRef, usr: User): Props = {
    Props(new WebSocketActor(out, usr))

  }
}
