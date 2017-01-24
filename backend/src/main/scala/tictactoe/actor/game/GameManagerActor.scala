package tictactoe.actor.game

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.model.impl.Messages

/**
  */
class GameManagerActor(p1: (String, ActorRef), p2: (String, ActorRef)) extends Actor {

  val game = GameWrapper


  override def receive: Receive = {

    case _ =>
  }


}

object GameManagerActor {


  def props(p1: (String, ActorRef), p2: (String, ActorRef)): Props =
    Props(new GameManagerActor(p1: (String, ActorRef), p2: (String, ActorRef)))

}

private class GameWrapper {

  private val game: TicTacToe = TicTacToe.apply()
  private val cont = game.getController
  cont.setPlayers("p0", "p1")
  var moveCounter = 0

  def move(playerNo: Int, row: Int, column: Int, grid: Int): GameWrapper.Status = {
    val current = moveCounter % 2
    if (playerNo != current)
      return GameWrapper.NotYourTurn
    cont.setValue(row, column, grid)
    if (cont.getStatus == Messages.CELL_IS_SET)
      return GameWrapper.InvalidMove

    moveCounter += 1
    if (cont.getWin(current))
      GameWrapper.Won
    else if (moveCounter == GameWrapper.MaxMoves)
      GameWrapper.Tie
    else
      GameWrapper.Moved


  }


}

private object GameWrapper {
  private final val MaxMoves: Int = 3 * 3 * 3

  sealed trait Status

  case object Won extends Status

  case object Tie extends Status

  case object Moved extends Status

  case object InvalidMove extends Status

  case object NotYourTurn extends Status


}

