package tictactoe.actor

import java.util

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
    context.become(InitFunction)
    userManager ! UserTokenManagerActor.RegisterForUser(user)
  }


  override def postStop(): Unit = {
    userHandler.foreach(_.handler ! UserHandlerActor.UnRegisterWebSocket())
  }

  object InitFunction extends PartialFunction[Any, Unit] {
    private val q: util.Queue[(Any, ActorRef)] = new util.LinkedList()

    private val pf: PartialFunction[Any, Unit] = {
      case KeepAlive(_) => handleKeepAlive()
      case UserTokenManagerActor.UserHandlerIfPresent(opt) => setUserHandler(opt)
      case any => q.add((any, sender()))
    }

    private def setUserHandler(opt: Option[UserHandlerContainer]): Unit = {
      opt match {
        case None => throw new IllegalArgumentException("userHandler must be set!")
        case some => userHandler = some
      }

      if (q.isEmpty) {
        context.unbecome()
      } else {
        val first = q.peek()
        val poll: PartialFunction[Any, Unit] = {
          case `first` =>
            context.unbecome()
            defaultReceive(first)

          case any => self.!(any)(sender())
        }

        context.become(poll, discardOld = true)

        while (!q.isEmpty) {
          val n = q.poll()
          self.!(n._1)(n._2)
        }
      }
    }


    override def isDefinedAt(x: Any): Boolean = pf.isDefinedAt(x)

    override def apply(v1: Any): Unit = pf(v1)
  }


  private val defaultReceive: PartialFunction[Any, Unit] = {
    case msg: String => handleMsg(msg)
    case BroadcastMessage(msg) => handleBroadcastMessage(msg)

    case any => error(s"Invalid message: + $any")
      throw new IllegalArgumentException(s"Invalid message: + $any")
  }


  def handleBroadcastMessage(msg: String): Unit = {
    info(s"Out ${user.name}:  $msg")
    out ! msg
  }


  override def receive: Receive = defaultReceive

  object WithUserHandler {
    def apply(block: (UserHandlerContainer) => Unit, msg: String): Unit = {
      info(s"IN  ${user.name}:  $msg")
      userHandler match {
        case None => throw new IllegalStateException("no UserHandler")
        case Some(cont) => block(cont)
      }
    }
  }


  def handleMsg(msg: String): Unit = {
    try {
      val json: JsType = Json.parse(msg)
      json match {
        case KeepAlive(_) => handleKeepAlive()
        case UserStatusMSG(_) => WithUserHandler(handleUserStatus(), msg)
        case AskForGame(value) => WithUserHandler(handleAskForGame(value), msg)
        case GameRequested(value) => WithUserHandler(handleGameRequested(value), msg)
        case GameStatus(_) => WithUserHandler(handleGameStatus(), msg)
        case GamePlayers(_) => WithUserHandler(handleGamePlayers(), msg)
        case Move(value) => WithUserHandler(handleMove(value), msg)
        case DirectMessage(value) => WithUserHandler(handleMessage(value), msg)
        case any =>
          info(s"IN  ${user.name}:  $msg")
          throw new IllegalArgumentException(s"Invalid message: + $any")
      }
    } catch {
      case e: Exception =>
        warn(s"couldn't handle message: $msg. for user ${user.name} Exception: ", e)
    }
  }

  def handleKeepAlive(): Unit = {
    out ! KeepAlive.json()
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
