package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import tictactoe.actor.user.UserManagerActor._
import tictactoe.model.User
import util.FunctionalHelper.ofTuple
import util.TokenGenerator

import scala.collection.mutable

/**
  */
class UserManagerActor private() extends Actor {
  private val tokenGen = new TokenGenerator()
  private val tokenCache = mutable.HashMap.empty[String, Container]


  // key = user.email
  private val loggedInUserCache = mutable.Map.empty[String, Container]

  override def receive: Receive = {
    case SubscribeToUserAnnouncement(usr) => addSubscriberToCache(usr)
    case UnSubscribeFromUserAnnouncement(usr) => removeSubscriberFromCache(usr)
    case AllLoggedInRequest(email: String) => handleAllLoggedInRequest(email: String)
    case AskUserForGame(token: String, sender: User) => handleAskUserForGame(token: String, sender: User)
    case AcceptAndStartGame(p1: User, p2Token: String, accept: Boolean) => handleAcceptAndStartGame(p1: User, p2Token: String, accept: Boolean)
  }


  def handleAllLoggedInRequest(email: String): Unit = {
    val userTokenList = loggedInUserCache.filterNot(_._1 == email).map(ofTuple((_, cont) => (cont.usr.name, cont.token))).toList
    tokenCache.get(email).foreach(cont =>
      sender() ! AllLoggedInReturn(cont.token, userTokenList, None)
    )
  }

  def addSubscriberToCache(user: User): Unit = {
    loggedInUserCache.getOrElse(user.email, createContainer(user)) + sender()
  }

  private def createContainer(user: User): Container = {
    val cont = new Container(user, tokenGen.generateToken(user.name))

    tokenCache.put(cont.token, cont)
    messageSubscribers(LoggedInAnnouncement(user.name, cont.token))
    cont
  }

  def removeSubscriberFromCache(user: User): Unit = {
    loggedInUserCache.get(user.email).map(_ - sender()).filter(_.isEmpty).foreach(removeContainer)
  }

  def removeContainer(container: Container): Unit = {
    loggedInUserCache.remove(container.usr.email)
    tokenCache.remove(container.token)
    messageSubscribers(LoggedOutAnnouncement(container.usr.name, container.token))
  }

  def messageSubscribers(message: Any): Unit = {
    loggedInUserCache.foreach(ofTuple((_, cont) => cont.subscribers.foreach(_ ! message)))
  }


  def handleAskUserForGame(token: String, sender: User): Unit = {
    tokenCache.get(token).zip(loggedInUserCache.get(sender.email)).foreach(ofTuple((rec, send) => {
      val ret = AskUserForGameForward(sender.name, send.token)
      rec.subscribers.foreach(sub => sub ! ret)
    }))
  }

  def handleAcceptAndStartGame(p1: User, p2Token: String, accept: Boolean): Unit = {

    //FIXME start game

    tokenCache.get(p2Token).zip(loggedInUserCache.get(p1.email)).foreach(ofTuple((rec, send) => {
      val ret = AcceptAndStartGameForward(p1.name, send.token, accept)
      rec.subscribers.foreach(sub => sub ! ret)
    }))
  }

}

private class Container(val usr: User,
                        val token: String,
                        var gameManagerOpt: Option[ActorRef] = None,
                        var subscribers: Set[ActorRef] = Set.empty
                       ) {

  def +(ref: ActorRef): Container = {
    subscribers = subscribers + ref
    this
  }


  def -(ref: ActorRef): Container = {
    subscribers = subscribers - ref
    this
  }

  def isEmpty: Boolean = subscribers.isEmpty

}


object UserManagerActor {

  case class AskUserForGame(token: String, sender: User)

  case class AskUserForGameForward(senderName: String, senderToken: String)

  case class AcceptAndStartGame(p1: User, p2Token: String, accept: Boolean)

  case class AcceptAndStartGameForward(senderName: String, senderToken: String, accept: Boolean)

  case class AllLoggedInRequest(email: String)

  case class AllLoggedInReturn(usrToken: String, userTokenList: List[(String, String)], gameManager: Option[ActorRef])

  case class LoggedInAnnouncement(user: String, token: String)

  case class LoggedOutAnnouncement(user: String, token: String)

  final val NAME = "UserManager"

  case class SubscribeToUserAnnouncement(user: User)

  case class UnSubscribeFromUserAnnouncement(user: User)


  def apply(): Props = {
    Props(new UserManagerActor())
  }
}



