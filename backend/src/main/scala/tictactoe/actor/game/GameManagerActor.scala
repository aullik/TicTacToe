package tictactoe.actor.game

import akka.actor.{Actor, Props}
import tictactoe.actor.game.GameManagerActor.{AskIfUserIngame, AskIfUserIngameReturn}
import tictactoe.model.User

/**
  */
class GameManagerActor extends Actor {


  override def receive: Receive = {
    case AskIfUserIngame(usr) => handleAskIfUserIngame(usr)
  }


  def handleAskIfUserIngame(usr: User): Unit = {
    //fIXME add logic
    sender() ! AskIfUserIngameReturn(false)
  }
}

object GameManagerActor {

  case class AskIfUserIngame(user: User)

  case class AskIfUserIngameReturn(inGame: Boolean)

  final val NAME = "gameManager"

  def props: Props = Props(new GameManagerActor())

}
