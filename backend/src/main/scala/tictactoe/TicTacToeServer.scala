package tictactoe

import java.util.Locale

import com.google.inject.Inject
import tictactoe.persistence.PersistenceEnvironment

object TicTacToeServer {

  Locale.setDefault(Locale.GERMAN)

  @Inject
  private lazy val environment: PersistenceEnvironment = ???

  lazy val persistence = environment.persistence

}



