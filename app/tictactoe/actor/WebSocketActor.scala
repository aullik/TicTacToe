package tictactoe.actor

import akka.actor._
import grizzled.slf4j.Logging
import org.json4s.JsonAST.JObject
import org.json4s._
import org.json4s.jackson.Serialization
import play.api.libs.json.Json
import tictactoe.actor.user.UserManagerActor
import tictactoe.actor.user.UserManagerActor.{AcceptAndStartGameForward, AllLoggedInReturn, AskUserForGameForward}
import tictactoe.model.User
import util.FunctionalHelper.ofTuple


/**
  */
class WebSocketActor(out: ActorRef, user: User) extends Actor with Logging {


  private implicit val formats = Serialization.formats(NoTypeHints)

  private val userManager = context.actorSelection(context.system / UserManagerActor.NAME)

  userManager ! UserManagerActor.SubscribeToUserAnnouncement(user)

  var gameManagerOpt: Option[ActorRef] = None

  object ExtendedFunction extends PartialFunction[Any, Unit] {


    private val pf: PartialFunction[Any, Unit] = {
      case msg: String => handleMsg(msg)
      case UserManagerActor.LoggedInAnnouncement(username, usertoken) => handleLoggedInAnnouncement(username, usertoken)
      case UserManagerActor.LoggedOutAnnouncement(username, usertoken) => handleLoggedOutAnnouncement(username, usertoken)
      case AskUserForGameForward(senderName: String, senderToken: String) => handleAskUserForGameForward(senderName: String, senderToken: String)
      case AllLoggedInReturn(usrToken, userTokenList, gameManager) => handleAllLoggedInReturn(usrToken, userTokenList, gameManager)
      case AcceptAndStartGameForward(senderName: String, senderToken: String, accept: Boolean) => handleAcceptAndStartGameForward(senderName: String, senderToken: String, accept: Boolean)


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
  }

  def handleAllLoggedInReturn(usrToken: String, userTokenList: List[(String, String)], gameManager: Option[ActorRef]): Unit = {
    gameManagerOpt = gameManager
    UserStatus(user.name, usrToken, gameManager.isDefined, userTokenList.map(ofTuple((usr, token) => UserElement(usr, token))))
  }

  def handleAskForGame(value: UserElement): Unit = {
    userManager ! UserManagerActor.AskUserForGame(value.token, user)
  }

  def handleAskUserForGameForward(senderName: String, senderToken: String): Unit = {
    out ! GameRequested.toJson(UserElement(senderName, senderToken))
  }

  def handleGameRequested(value: AcceptGame): Unit = {
    userManager ! UserManagerActor.AcceptAndStartGame(user, value.token, value.accept)
  }

  def handleAcceptAndStartGameForward(senderName: String, senderToken: String, accept: Boolean) {
    out ! AskForGame.toJson(AcceptGame(senderName, senderToken, accept))
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