package tictactoe.akka

import java.io.File
import javax.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.stream.ActorMaterializer
import com.google.inject.Provider
import com.typesafe.config.ConfigFactory
import grizzled.slf4j.Logging
import play.api.libs.json.Json
import play.api.mvc.Controller
import tictactoe.{TicTacToeServer, TicTacToeServerModule}
import tictactoe.actor.messages.UserElement
import tictactoe.actor.user.{UserHandlerActor, UserHandlerContainer}
import tictactoe.controllers.webControllers.{WebController, WebControllerContainer}
import tictactoe.model.entity.UserId
import tictactoe.persistence.{Persistence, PersistenceEnvironment, ProductivePersistenceEnvironmentSlick}
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor._
import tictactoe.persistence.databaseAccess.slick.SlickPersistence

import scala.collection.mutable
import scala.io.StdIn



object WebServer extends App{
  val conf = new File("web/conf/application.conf")
  implicit val system = ActorSystem("systemTwo", ConfigFactory.parseFile(conf).resolve())
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher
  //private val lobbyCache = mutable.Map.empty[String, UserElement]

  //final val persistence: Persistence = environment.persistence
  val server = new TicTacToeServer

  val route =
    path("users") {
      get {
        println()
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`,server.persistence.userConnection.findAllDocuments().toString()))// Json.toJson(lobbyCache.values.toList).toString()
      }
    }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 55)

    println(s"Server online at http://localhost:55/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

}


