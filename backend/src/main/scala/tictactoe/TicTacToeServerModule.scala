package tictactoe

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import tictactoe.persistence.{DevelopmentPersistenceEnvironment, PersistenceEnvironment}

/** Configuration for dependency injection with Guice
  *
  */
class TicTacToeServerModule extends AbstractModule with ScalaModule {

  def configure(): Unit = {
    bind[PersistenceEnvironment].toInstance(DevelopmentPersistenceEnvironment)
  }

}
