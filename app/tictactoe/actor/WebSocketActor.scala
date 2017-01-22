package tictactoe.actor

import akka.actor._
import akka.pattern.ask
import grizzled.slf4j.Logging
import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.jackson.Serialization
import play.api.libs.json.Json
import tictactoe.actor.game.GameManagerActor
import tictactoe.actor.game.GameManagerActor.AskIfUserIngameReturn
import tictactoe.actor.user.UserManagerActor
import tictactoe.actor.user.UserManagerActor.AllLoggedInReturn
import tictactoe.model.User
import util.FunctionalHelper.ofTuple

import scala.concurrent.Future


/**
  */
class WebSocketActor(out: ActorRef, user: User) extends Actor with Logging {


  private implicit val formats = Serialization.formats(NoTypeHints)

  private val userManager = context.actorSelection(context.system / UserManagerActor.NAME)
  private val gameManager = context.actorSelection(context.system / GameManagerActor.NAME)

  userManager ! UserManagerActor.SubscribeToUserAnnouncement(user)

  object ExtendedFunction extends PartialFunction[Any, Unit] {


    private val pf: PartialFunction[Any, Unit] = {
      case msg: String => handleMsg(msg)
      case UserManagerActor.LoggedInAnnouncement(username, usertoken) => handleLoggedInAnnouncement(username, usertoken)
      case UserManagerActor.LoggedOutAnnouncement(username, usertoken) => handleLoggedOutAnnouncement(username, usertoken)
      case us: UserStatus => out ! UserStatus.toJson(us)
      case any => warn(s"illegal message + $any")
        throw new IllegalArgumentException("Invalid message")
    }

    override def isDefinedAt(x: Any): Boolean = pf.isDefinedAt(x)

    override def apply(v1: Any): Unit =
      try {
        pf.apply(v1)
      } catch {
        case t: Throwable => error("Exception in actor: " + t)
          throw t
      }
  }

  def receive = ExtendedFunction

  def handleMsg(msg: String): Unit = {
    try Json.parse(msg) match {
      case UserStatus(_) => handleUserStatus()
      case AskForGame(value) => handleAskForGame(value)
      case GameRequested(value) => handleGameRequested(value)
      case GameStatus(_) => handleGameStatus()
      case GamePlayers(_) => handleGamePlayers()
      case Move(value) => handleMove(value)
      case DirectMessage(value) => handleMessage(value)
      case any => throw new IllegalArgumentException(s"Invalid message: + $any")
    } catch {
      case e: Exception =>
        warn(s"couldn't handle message: $msg. Exception: $e")
    }
  }


  def handleLoggedInAnnouncement(user: String, token: String): Unit = {
    out ! UserLoggedIn.toJson(UserElement(user, token))
  }

  def handleLoggedOutAnnouncement(user: String, token: String): Unit = {
    out ! UserLoggedOut.toJson(UserElement(user, token))
  }

  def handleUserStatus(): Unit = {
    userManager ! UserManagerActor.AllLoggedInRequest(user.email)
    gameManager ! GameManagerActor.AskIfUserIngame(user)

    val allLoggedIn: Future[AllLoggedInReturn] = (userManager ? UserManagerActor.AllLoggedInRequest(user.email)).mapTo[UserManagerActor.AllLoggedInReturn]
    val inGame: Future[AskIfUserIngameReturn] = (gameManager ? GameManagerActor.AskIfUserIngame(user)).mapTo[GameManagerActor.AskIfUserIngameReturn]

    allLoggedIn.flatMap(loggedin => inGame.map(game => {
      self ! UserStatus(user.name, loggedin.usrToken, game.inGame, loggedin.userTokenList.map(ofTuple((usr, token) => UserElement(usr, token))))
    }))

    out ! UserStatus.toJson(UserStatus("alice", "aliceToken", inGame = false, List(UserElement("bob", "bobToken"))))
  }


  def handleGameRequested(value: AcceptGame): Unit = {
    //send this to the other player
    out ! AskForGame.toJson(AcceptGame("bob", "token", accept = true))
  }

  def handleAskForGame(value: UserElement): Unit = {
    out ! AcceptGame(value.name, value.token, accept = true)
  }

  def handleGameStatus(): Unit = {
    out ! GameStatus.toJson(GameStatus(PlayerMove.list("M-1-2-3", "O-2-2-2")))
  }

  def handleGamePlayers(): Unit = {
    out ! GamePlayers.toJson(GamePlayers(UserElement("alice", "aliceToken"), UserElement("bob", "bobToken")))
  }

  //FIXME only for testing
  var finishNextTurn = false

  def handleMove(value: Move): Unit = {
    if (finishNextTurn)
      out ! GameFinish.toJson(GameFinish("M-" + value.move, tie = true))
    else {
      finishNextTurn = true
      out ! PlayerMove.toJson(PlayerMove("M-" + value.move))
    }

  }

  def handleMessage(value: DirectMessage): Unit = {
    out ! DirectMessage.toJson(value)
  }


  override def postStop(): Unit = {
    userManager ! UserManagerActor.UnSubscribeFromUserAnnouncement(user)
  }
}

object WebSocketActor {

  val CHANNEL = "userChannel"

  val NOVALUE: JObject = JObject(Nil)

  val MSG_TYPE = "msgType"
  val VALUE = "value"

  def apply(out: ActorRef, usr: User): Props = {
    Props(new WebSocketActor(out, usr))
  }
}