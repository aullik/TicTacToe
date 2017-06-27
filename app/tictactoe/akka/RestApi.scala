package tictactoe.akka

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import tictactoe.TicTacToeServer
import tictactoe.model.entity.UserId
import play.api.libs.concurrent.Execution.Implicits._

import scala.io.StdIn
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import org.bson.Document
import spray.json._


// collect your json format instances into a support trait:
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val userFormat = jsonFormat3(MyUser)
}

final case class MyUser(name: String, email: String,password: String)


trait RestService extends Directives with JsonSupport {
  implicit val system:ActorSystem
  implicit val materializer:ActorMaterializer
  implicit val server: TicTacToeServer
  implicit val passwordHasherRegistry: PasswordHasherRegistry

  val route =

    (path("users") & get ) {
      complete (server.persistence.userConnection.findAllDocuments().map(_.toJson))
    }~
      pathPrefix("users") {
        (path("id" / Segment) & get) { id =>
          println("id  " + UserId.ofStringID(id))
          complete(server.persistence.userConnection.findFirstDocumentForID(UserId.ofStringID(id)).map(_.toJson))
        }~
          (path("email"/ Segment) & get ) { email =>
            complete (server.persistence.userConnection.findFirstDocumentForEmail(email).map(_.toJson))
          }
      }~
      (path("users") & post ) {
        entity(as[MyUser]) { user =>
          complete(server.persistence.userConnection.writeDocument(toDocument(user)).toJson)
        }
      }
  private def toDocument(user:MyUser): Document = {
    val doc = new Document()
    doc.append("_id", UserId().asString)
    doc.append("name", user.name)
    doc.append("email", user.email)
    doc.append("emailConfirmed", false)
    doc.append("key", user.email)
    doc.append("passwordHash", passwordHasherRegistry.current.hash(user.password).password)
    doc
  }
}

class RestServer(serv:TicTacToeServer, passwordHash:PasswordHasherRegistry) extends RestService{
  val conf = new File("web/conf/application.conf")
  implicit val system = ActorSystem("systemTwo", ConfigFactory.parseFile(conf).resolve())
  implicit val materializer = ActorMaterializer()
  implicit val server:TicTacToeServer = serv
  implicit val passwordHasherRegistry = passwordHash

  def startServer(): Unit = {
    var bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:55/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }

}

/*
class RestApi (server: TicTacToeServer){

  val conf = new File("web/conf/application.conf")
  implicit val system = ActorSystem("systemTwo", ConfigFactory.parseFile(conf).resolve())
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    (path("users") & get ) {
      complete (server.persistence.userConnection.findAllDocuments().toString)
    }~
    pathPrefix("users") {
      (path("id" / Segment) & get) { id =>
        println("id  " + UserId.ofStringID(id))
        complete(server.persistence.userConnection.findFirstDocumentForID(UserId.ofStringID(id)).map(_.toJson))
      }~
      (path("email"/ Segment) & get ) { email =>
        complete (server.persistence.userConnection.findFirstDocumentForEmail(email).map(_.toJson))
      }
    }~
    (path("users") & post ) {
      entity(as[User]) { user =>
        //      server.persistence.userManager.add(usr)  passwordHasherRegistry.current.hash(signUpData.password)
        complete(user.toString)
      }
    }

    def startRestApi(): Unit = {
      var bindingFuture = Http().bindAndHandle(route, "localhost", 55)
      println(s"Server online at http://localhost:55/\nPress RETURN to stop...")
      StdIn.readLine() // let it run until user presses return
      bindingFuture
        .flatMap(_.unbind()) // trigger unbinding from the port
        .onComplete(_ => system.terminate()) // and shutdown when done
    }

}
*/