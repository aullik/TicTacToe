package tictactoe

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import tictactoe.persistence.{DevelopmentPersistenceEnvironment, PersistenceEnvironment}

/** Configuration for dependency injection with Guice
  *
  */
class TicTacToeModule extends AbstractModule with ScalaModule {

  def configure(): Unit = {
    bind[TicTacToeServer].to[TicTacToeServer]
    bind[PersistenceEnvironment].toInstance(DevelopmentPersistenceEnvironment)
  }


}
