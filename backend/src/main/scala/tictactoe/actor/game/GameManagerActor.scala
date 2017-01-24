package tictactoe.actor.game

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.model.impl.Messages
import grizzled.slf4j.Logging
import tictactoe.actor.game.GameManagerActor.{DoMove, InvalidMove, TokenEndMove, TokenMoved}

import scala.util.{Success, Try}

/**
  */
class GameManagerActor(p0: (String, ActorRef), p1: (String, ActorRef)) extends Actor with Logging {

  private val game = new GameWrapper()


  def handleDoMove(move: String): Unit = {
    val ret = sender()
    parsePlayer(ret) match {
      case None =>
        throw new IllegalStateException("got move from bad player")
      //illegal Sender
      case Some(tuple) => handlePlayerMove(move, ret, tuple._1, tuple._2)
    }
  }

  def handlePlayerMove(move: String, playerRef: ActorRef, playerNo: Int, token: String) {
    parseMove(move) match {
      case None =>
        broadCast(InvalidMove(token, move))
      case Some(mv) =>
        game.move(playerNo, mv._1, mv._2, mv._3) match {
          case GameWrapper.Won => broadCast(TokenEndMove(token, move, tie = false))
          case GameWrapper.Tie => broadCast(TokenEndMove(token, move, tie = true))
          case GameWrapper.Moved => broadCast(TokenMoved(token, move))
          case GameWrapper.InvalidMove => broadCast(InvalidMove(token, move))
          case GameWrapper.NotYourTurn => broadCast(InvalidMove(token, move))
        }
    }
  }

  def broadCast(message: Any): Unit = {
    p0._2 ! message
    p1._2 ! message
  }


  private def parsePlayer(send: ActorRef): Option[(Int, String)] = {
    if (send == p0._2) Some((0, p0._1))
    else if (send == p1._2) Some((1, p1._1))
    else None
  }


  private def parseMove(move: String): Option[(Int, Int, Int)] = {
    val vec = move.split("-").toStream.map(i => Try(Integer.parseInt(i))).collect {
      case Success(i) => i
    }.filter(_ >= 0).toVector

    if (vec.size == 3)
      Some((vec(0), vec(1), vec(2)))
    else
      None
  }

  override def receive: Receive = {
    case DoMove(move: String) => handleDoMove(move: String)

    case any =>
      warn(s"illegal message + $any")
      throw new IllegalArgumentException("Invalid message")
  }


}

object GameManagerActor {

  case class DoMove(move: String)

  case class TokenMoved(token: String, move: String)

  case class TokenEndMove(token: String, move: String, tie: Boolean)

  case class InvalidMove(token: String, move: String)


  def props(p0: (String, ActorRef), p1: (String, ActorRef)): Props =
    Props(new GameManagerActor(p0: (String, ActorRef), p1: (String, ActorRef)))

}

private class GameWrapper {

  private val game: TicTacToe = TicTacToe.apply()
  private val cont = game.getController
  cont.setPlayers("p0", "p1")
  var moveCounter = 0

  def move(playerNo: Int, row: Int, column: Int, grid: Int): GameWrapper.Status = {
    if (moveCounter == GameWrapper.MaxMoves)
      return GameWrapper.InvalidMove
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

