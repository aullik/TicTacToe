package tictactoe

import java.util.Locale

import com.google.inject.Inject
import tictactoe.exceptions.ShouldBeInjectedException
import tictactoe.persistence.PersistenceEnvironment

object TicTacToeServer {

  Locale.setDefault(Locale.GERMAN)

  @Inject
  private lazy val environment: PersistenceEnvironment = throw ShouldBeInjectedException()

  lazy val persistence = environment.persistence

}



