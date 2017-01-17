package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import tictactoe.actor.user.UserManagerActor.{SubscribeToUserAnnouncement, UnSubscribeFromUserAnnouncement}
import tictactoe.model.User
import util.ListUtils

import scala.collection.mutable

/**
  */
class UserManagerActor private() extends Actor {


  override def receive: Receive = {
    case SubscribeToUserAnnouncement(usr) =>
    case UnSubscribeFromUserAnnouncement(usr) =>
  }

  //  mediator ! Subscribe(CHANNEL, self)
  //  val mediator = DistributedPubSub(context.system).mediator

  val loggedInUserCache = mutable.Map.empty[String, User]
}

private case class Container(usr: User, subscribers: List[ActorRef]) {

  def add(ref: ActorRef): Container = {
    copy(subscribers = ref :: subscribers)
  }


  def remove(ref: ActorRef): Container = {
    copy(subscribers = ListUtils.removeFirst(ref, subscribers))
  }
}


object UserManagerActor {

  val NAME = "UserManager"

  case class SubscribeToUserAnnouncement(user: User)

  case class UnSubscribeFromUserAnnouncement(user: User)


  def apply() = {
    Props(new UserManagerActor())
  }
}



