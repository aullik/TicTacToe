package tictactoe.actor.messages

import play.api.libs.json._

/**
  */
object WebSocketActorMessages {
}

sealed trait SocketObject {
  protected final val MSG_TYPE = "msgType"
  protected final val VALUE = "value"
}

sealed trait InMessage extends SocketObject {
  self =>

  val inMsg: String
  type inValue

  def getValue(values: JsValue)(implicit format: OFormat[inValue]): inValue = {
    values.validate[inValue](format).get
  }

  def unapply(jsonValue: JsValue)(implicit oFormat: OFormat[inValue]): Option[inValue] = {
    (jsonValue \ MSG_TYPE).validate[String].asOpt.flatMap {
      case this.inMsg => (jsonValue \ VALUE).validate[inValue].asOpt
      case _ => None
    }
  }
}

object InMessage {
}

trait OutMessage extends SocketObject {
  val outMsg: String
  type outValue

  def toJson(value: outValue)(implicit oFormat: OFormat[outValue]): String = {
    s"{$MSG_TYPE:$outMsg,$VALUE:${Json.toJson(value)}"
  }

}

object OutMessage {
}


case class AcceptGame(name: String, token: String, accept: Boolean)

object AcceptGame extends SocketObject {
  implicit val form: OFormat[AcceptGame] = Json.format[AcceptGame]
}

case class UserElement(name: String, token: String)

object UserElement extends SocketObject {
  implicit val form: OFormat[UserElement] = Json.format[UserElement]
}

//created so you don't have to work with EmptyMessage.typ
sealed trait EmptyMessage

object EmptyMessage extends EmptyMessage {
  implicit val form: OFormat[EmptyMessage] = OFormat.apply(
    (js: JsValue) => js match {
      case JsObject(a) =>
        if (a.isEmpty)
          JsSuccess(this)
        else
          JsError("")

      case _ => JsError("")
    }, (_: EmptyMessage) => {
      JsObject(Seq.empty)
    })
}

case class UserStatus(name: String,
                      token: String,
                      users: List[UserElement]
                     )

object UserStatus extends InMessage with OutMessage {
  implicit val form: OFormat[UserStatus] = Json.format[UserStatus]

  override type inValue = EmptyMessage
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
  override type outValue = EmptyMessage
}

case class PlayerMove(pMove: String)

object PlayerMove extends OutMessage {
  implicit val form: OFormat[PlayerMove] = Json.format[PlayerMove]
  override val outMsg: String = "playerMoved"
  override type outValue = PlayerMove

  def list(pMove: String*): List[PlayerMove] = {
    pMove.toList.map(PlayerMove(_))
  }
}

case class GameStatus(moves: List[PlayerMove])

object GameStatus extends InMessage with OutMessage {
  implicit val form: OFormat[GameStatus] = Json.format[GameStatus]

  override val inMsg: String = "gameStatus"
  override type inValue = EmptyMessage
  override val outMsg: String = "gameStatusRet"
  override type outValue = GameStatus

}

case class GamePlayers(me: UserElement, other: UserElement)

object GamePlayers extends InMessage with OutMessage {

  implicit val form: OFormat[GamePlayers] = Json.format[GamePlayers]
  override val inMsg: String = "gamePlayers"
  override type inValue = EmptyMessage
  override val outMsg: String = "gamePlayersRet"
  override type outValue = GamePlayers

}

case class Move(move: String)

object Move extends InMessage {
  implicit val form: OFormat[Move] = Json.format[Move]

  override val inMsg: String = "move"
  override type inValue = Move

}


case class GameFinish(pMove: String, tie: Boolean)

object GameFinish extends OutMessage {
  implicit val form: OFormat[GameFinish] = Json.format[GameFinish]

  override val outMsg: String = "gameFinish"
  override type outValue = GameFinish
}

case class DirectMessage(avatarColor: String, timestamp: String, body: String)

object DirectMessage extends InMessage with OutMessage {
  implicit val form: OFormat[DirectMessage] = Json.format[DirectMessage]
  override val inMsg: String = "message"
  override type inValue = DirectMessage
  override val outMsg: String = "acceptMessage"
  override type outValue = DirectMessage

}


