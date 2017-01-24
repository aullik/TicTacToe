package tictactoe.actor.messages

import play.api.libs.json.{Json, OFormat}

/**
  */
case class DirectMessage(avatarColor: String, timestamp: String, body: String)

object DirectMessage {
  implicit val form: OFormat[DirectMessage] = Json.format[DirectMessage]
}