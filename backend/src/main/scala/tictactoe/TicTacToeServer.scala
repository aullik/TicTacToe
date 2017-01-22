package tictactoe

import java.util.Locale
import javax.inject.Singleton

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.Inject
import tictactoe.actor.user.UserManagerActor
import tictactoe.persistence.{Persistence, PersistenceEnvironment}

@Singleton
class TicTacToeServer @Inject() private(system: ActorSystem, environment: PersistenceEnvironment) {

  Locale.setDefault(Locale.GERMAN)

  private val userControllerActor: ActorRef = system.actorOf(UserManagerActor())

  val persistence: Persistence = environment.persistence

}



