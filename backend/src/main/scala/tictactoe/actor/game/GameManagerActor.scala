package tictactoe.actor.game

import akka.actor.{Actor, Props}

/**
  */
class GameManagerActor extends Actor {


  override def receive: Receive = {
    case _ =>
  }


}

object GameManagerActor {


  def props: Props = Props(new GameManagerActor())

}
