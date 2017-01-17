package actor

import play.api.libs.json.{JsValue, Json, OFormat}

import scala.collection.mutable

/**
  */
object WebSocketActorMessages {

}


sealed trait InMessage {
  self =>

  InMessage.map.put(self.inMsg, self)

  val inMsg: String
  type inValue

  def getValue(values: JsValue)(implicit format: OFormat[inValue]): inValue = {
    values.validate[inValue](format).get
  }
}

object InMessage {
  private[InMessage] val map = mutable.Map.empty[String, InMessage]

  def getMessage(s: String): InMessage = {
    map.get(s) match {
      case Some(ret) => ret
      case None => throw new IllegalArgumentException(s"This is no valid message: $s")
    }
  }
}

trait OutMessage {
  val outMsg: String
  type outValue
}

case class UserStatus(name: String,
                      token: String,
                      inGame: Boolean,
                      users: List[UserElement]
                     )

object UserStatus extends InMessage with OutMessage {
  implicit val form = Json.format[UserStatus]

  override type inValue = Nothing
  override type outValue = UserStatus

  override val inMsg = "userStatus"
  override val outMsg = "userStatusRet"
}

object UserLoggedIn extends OutMessage {
  override val outMsg: String = "userLoggedIn"
  override type outValue = UserElement
}

object UserLoggedOut extends OutMessage {
  override val outMsg: String = "userLoggedOut"
  override type outValue = UserElement
}


object AskForGame extends InMessage with OutMessage {
  override val inMsg: String = "askForGame"
  override type inValue = UserElement
  override val outMsg: String = "askForGameRet"
  override type outValue = AcceptGame
}

object GameRequested extends InMessage with OutMessage {
  override val inMsg: String = "gameRequestedRet"
  override type inValue = AcceptGame
  override val outMsg: String = "gameRequested"
  override type outValue = UserElement
}


object StartGame extends OutMessage {
  override val outMsg: String = "startGame"
  override type outValue = Nothing
}

case class PlayerMove(pMove: String)

object PlayerMove extends OutMessage {
  implicit val form = Json.format[PlayerMove]
  override val outMsg: String = "playerMoved"
  override type outValue = PlayerMove

  def list(pMove: String*): List[PlayerMove] = {
    pMove.toList.map(PlayerMove(_))
  }
}

case class GameStatus(moves: List[PlayerMove])

object GameStatus extends InMessage with OutMessage {
  implicit val form = Json.format[GameStatus]

  override val inMsg: String = "gameStatus"
  override type inValue = Nothing
  override val outMsg: String = "gameStatusRet"
  override type outValue = GameStatus
}

case class GamePlayers(me: UserElement, other: UserElement)

object GamePlayers extends InMessage with OutMessage {

  implicit val form = Json.format[GamePlayers]
  override val inMsg: String = "gamePlayers"
  override type inValue = Nothing
  override val outMsg: String = "gamePlayersRet"
  override type outValue = GamePlayers
}

case class Move(move: String)

object Move extends InMessage {
  implicit val form = Json.format[Move]

  override val inMsg: String = "move"
  override type inValue = Move
}


case class GameFinish(pMove: String, tie: Boolean)

object GameFinish extends OutMessage {
  implicit val form = Json.format[GameFinish]

  override val outMsg: String = "gameFinish"
  override type outValue = GameFinish
}

case class Message(avatarColor: String, timestamp: String, body: String)

object Message extends InMessage with OutMessage {
  implicit val form = Json.format[Message]
  override val inMsg: String = "message"
  override type inValue = Message
  override val outMsg: String = "acceptMessage"
  override type outValue = Message
}


case class AcceptGame(name: String, token: String, accept: Boolean)

object AcceptGame {
  implicit val form = Json.format[AcceptGame]
}

case class UserElement(name: String, token: String)

object UserElement {
  implicit val form = Json.format[UserElement]
}