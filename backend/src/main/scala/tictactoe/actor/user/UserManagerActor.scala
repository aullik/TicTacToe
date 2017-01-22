package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import tictactoe.actor.user.UserManagerActor.{LoggedInAnnouncement, LoggedOutAnnouncement, SubscribeToUserAnnouncement, UnSubscribeFromUserAnnouncement}
import tictactoe.model.User
import util.{FunctionalHelper, TokenGenerator}

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
  }

  def addSubscriberToCache(user: User): Unit = {
    val old = loggedInUserCache.getOrElse(user.email, createContainer(user))
    loggedInUserCache.update(user.email, old + sender())
  }

  private def createContainer(user: User): Container = {

    val cont =
      Container(user, tokenGen.generateToken(user.name))

    tokenCache.put(cont.token, cont)
    messageSubscribers(LoggedInAnnouncement(user.name, cont.token))

    cont
  }

  def removeSubscriberFromCache(user: User): Unit = {
    val key = user.email
    loggedInUserCache.get(key).foreach(old => {
      val changed = old - sender()
      if (old.isEmpty)
        removeContainer(old)
      else
        loggedInUserCache.update(key, changed)
    })
  }

  def removeContainer(container: Container): Unit = {
    loggedInUserCache.remove(container.usr.email)
    tokenCache.remove(container.token)
    messageSubscribers(LoggedOutAnnouncement(container.usr.name, container.usr.token))
  }

  def messageSubscribers(message: Any): Unit = {
    loggedInUserCache.foreach(FunctionalHelper.ofTuple((_, cont) => cont.subscribers.foreach(_ ! message)))
  }

}

private case class Container(usr: User, token: String, subscribers: Set[ActorRef] = Set.empty) {

  def +(ref: ActorRef): Container = {
    copy(subscribers = subscribers + ref)
  }


  def -(ref: ActorRef): Container = {
    copy(subscribers = subscribers - ref)
  }

  def isEmpty: Boolean = subscribers.isEmpty
}


object UserManagerActor {

  case class LoggedInAnnouncement(user: String, token: String)

  case class LoggedOutAnnouncement(user: String, token: String)

  val NAME = "UserManager"

  case class SubscribeToUserAnnouncement(user: User)

  case class UnSubscribeFromUserAnnouncement(user: User)


  def apply(): Props = {
    Props(new UserManagerActor())
  }
}



