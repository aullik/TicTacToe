package tictactoe.actor

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import tictactoe.actor.user.UserTokenManagerActor.{RequestUserHandlerForEmail, UserHandlerIfPresent}
import tictactoe.actor.user.{LobbyActor, UserTokenManagerActor}
import tictactoe.model.User
import tictactoe.model.entity.UserId

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor}

/**
  */
object TESTAPP extends App {
  val conf = new File("web/conf/application.conf")


  val system = ActorSystem("mySystem", ConfigFactory.parseFile(conf).resolve())
  implicit val ex: ExecutionContextExecutor = system.dispatcher
  implicit val timeout: Timeout = Timeout(10, TimeUnit.DAYS)

  val out = system.actorOf(Props(new OutActor()))

  val alice = User(UserId(), "alice", "alice@email.de", emailConfirmed = true)
  val bob = User(UserId(), "bob", "bob@email.de", emailConfirmed = true)

  private val userTokenManagerActor: ActorRef = system.actorOf(UserTokenManagerActor.props, UserTokenManagerActor.NAME)
  private val lobbyActor: ActorRef = system.actorOf(LobbyActor.props, LobbyActor.NAME)


  val websocketAlice = system.actorOf(WebSocketActor(out, alice))
  val websocketBob = system.actorOf(WebSocketActor(out, bob))

  Thread.sleep(100)

  val aliceToken = Await.result(userTokenManagerActor.?(RequestUserHandlerForEmail("alice@email.de")).mapTo[UserHandlerIfPresent], Duration.Inf).handlerOpt.get.token

  val msg = s"""{"msgType":"askForGame","value":{"name":"alice","token":"$aliceToken"}}"""
  println(msg)
  websocketBob ! msg


}


class OutActor extends Actor {

  override def receive: Receive = {
    case s: String => println(s)
  }
}
