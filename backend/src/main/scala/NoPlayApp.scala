import java.io.File

import akka.actor.ActorSystem
import com.google.inject.Guice
import com.typesafe.config.ConfigFactory
import net.codingwell.scalaguice.ScalaModule
import tictactoe.TicTacToeServerModule

/**
  */
object NoPlayApp extends App {

  val conf = new File("web/conf/application.conf")

  val system = ActorSystem("mySystem", ConfigFactory.parseFile(conf).resolve())
  val injector = Guice.createInjector(new TicTacToeServerModule, new PlayMockModule(system))

}

class PlayMockModule(system: ActorSystem) extends ScalaModule {
  override def configure(): Unit = {
    bind[ActorSystem].toInstance(system)
  }
}

