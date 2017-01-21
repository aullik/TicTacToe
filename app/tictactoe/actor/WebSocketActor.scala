package tictactoe.actor

import tictactoe.actor.WebSocketActor._
import akka.actor._

import grizzled.slf4j.Logging
import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization
import play.api.libs.json.{JsValue, Json, OFormat}
import tictactoe.actor.user.UserManagerActor
import tictactoe.model.User


/**
  */
class WebSocketActor(out: ActorRef, user: User) extends Actor with Logging {


  private implicit val formats = Serialization.formats(NoTypeHints)

  private val userManager = context.actorSelection(context.system / UserManagerActor.NAME)

  userManager ! UserManagerActor.SubscribeToUserAnnouncement(user)

  object ExtendedFunction extends PartialFunction[Any, Unit] {
    private val pf: PartialFunction[Any, Unit] = {
      case msg: String => handleMsg(msg)
      case any => warn(s"illegal message + $any")
        throw new IllegalArgumentException("Invalid message")
    }

    override def isDefinedAt(x: Any): Boolean = pf.isDefinedAt(x)

    override def apply(v1: Any): Unit =
      try {
        pf.apply(v1)
      } catch {
        case t: Throwable => error("Exception in actor: " + t)
          throw t
      }
  }

  def receive = ExtendedFunction

  def handleMsg(msg: String): Unit = {
    try {
      val jsonValue = Json.parse(msg)
      val msgType = (jsonValue \ MSG_TYPE).get.validate[String].get
      val values: JsValue = (jsonValue \ VALUE).get
      InMessage.getMessage(msgType) match {
        case UserStatus => handleUserStatus()
        case AskForGame => handleAskForGame(AskForGame.getValue(values))
        case GameRequested => handleGameRequested(GameRequested.getValue(values))
        case GameStatus => handleGameStatus()
        case GamePlayers => handleGamePlayers()
        case Move => handleMove(Move.getValue(values))
        case Message => handleMessage(Message.getValue(values))
        case _ => throw new IllegalArgumentException("Invalid message")
      }
    } catch {
      case e: Exception =>
        warn(s"couldn't handle message: $msg. Exception: $e")
    }
  }

  private def sendMessage[OM <: OutMessage](msg: OM, value: OM#outValue, sendTo: ActorRef = out)(implicit writer: OFormat[OM#outValue]): Unit = {
    sendTo ! compact(JObject(MSG_TYPE -> JString(msg.outMsg), VALUE -> Extraction.decompose(value)))
  }

  def handleUserStatus(): Unit = {
    sendMessage[UserStatus.type](UserStatus, UserStatus("alice", "aliceToken", inGame = false, List(UserElement("bob", "bobToken"))))
  }

  def handleGameRequested(value: AcceptGame): Unit = {
    //send this to the other player
    sendMessage[AskForGame.type](AskForGame, AcceptGame("bob", "token", accept = true))
  }

  def handleAskForGame(value: UserElement): Unit = {
    handleGameRequested(AcceptGame(value.name, value.token, accept = true))
  }

  def handleGameStatus(): Unit = {
    sendMessage[GameStatus.type](GameStatus, GameStatus(PlayerMove.list("M-1-2-2", "O-2-2-2")))
  }

  def handleGamePlayers(): Unit = {
    sendMessage[GamePlayers.type](GamePlayers, GamePlayers(UserElement("alice", "aliceToken"), UserElement("bob", "bobToken")))
  }

  //FIXME only for testing
  var finishNextTurn = false

  def handleMove(value: Move): Unit = {
    if (finishNextTurn)
      sendMessage[GameFinish.type](GameFinish, GameFinish("M-" + value.move, tie = true))
    else {
      finishNextTurn = true
      sendMessage[PlayerMove.type](PlayerMove, PlayerMove("M-" + value.move))
    }

  }

  def handleMessage(value: Message): Unit = {
    sendMessage[Message.type](Message, value)
  }


  override def postStop(): Unit = {
    userManager ! UserManagerActor.UnSubscribeFromUserAnnouncement(user)
  }
}

object WebSocketActor {


  val CHANNEL = "userChannel"

  val NOVALUE: JObject = JObject(Nil)

  val MSG_TYPE = "msgType"
  val VALUE = "value"

  def apply(out: ActorRef, usr: User): Props = {
    Props(new WebSocketActor(out, usr))
  }
}