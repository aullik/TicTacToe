package tictactoe

import java.util.Locale

import akka.actor.ActorSystem
import com.google.inject.Inject
import tictactoe.actor.user.UserControllerActor
import tictactoe.exceptions.ShouldBeInjectedException
import tictactoe.persistence.PersistenceEnvironment

object TicTacToeServer {

  Locale.setDefault(Locale.GERMAN)

  @Inject
  private val system: ActorSystem = throw ShouldBeInjectedException()

  @Inject
  private val environment: PersistenceEnvironment = throw ShouldBeInjectedException()

  val userControllerActor = system.actorOf(UserControllerActor())

  val persistence = environment.persistence

}



