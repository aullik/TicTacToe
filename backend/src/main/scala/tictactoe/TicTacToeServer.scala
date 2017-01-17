package tictactoe

import java.util.Locale
import javax.inject.Singleton

import akka.actor.ActorSystem
import com.google.inject.Inject
import tictactoe.actor.user.UserManagerActor
import tictactoe.persistence.PersistenceEnvironment

@Singleton
class TicTacToeServer @Inject() private(system: ActorSystem, environment: PersistenceEnvironment) {

  Locale.setDefault(Locale.GERMAN)

  val userControllerActor = system.actorOf(UserManagerActor())

  val persistence = environment.persistence

}



