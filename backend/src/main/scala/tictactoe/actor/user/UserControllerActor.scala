package tictactoe.actor.user

import akka.actor.{Actor, ActorRef, Props}
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.Subscribe
import tictactoe.actor.user.UserControllerActor.{CHANNEL, SubscribeToUserAnnouncement, UnSubscribeFromUserAnnouncement}
import tictactoe.model.User
import util.ListUtils

import scala.collection.mutable

/**
  */
class UserControllerActor private() extends Actor {

  val mediator = DistributedPubSub(context.system).mediator

  val loggedInUserCache = mutable.Map.empty[String, User]

  mediator ! Subscribe(CHANNEL, self)

  override def receive: Receive = {
    case SubscribeToUserAnnouncement(usr) =>
    case UnSubscribeFromUserAnnouncement(usr) =>
  }
}

private case class Container(usr: User, subscribers: List[ActorRef]) {

  def add(ref: ActorRef): Container = {
    copy(subscribers = ref :: subscribers)
  }


  def remove(ref: ActorRef): Container = {
    copy(subscribers = ListUtils.removeFirst(ref, subscribers))
  }
}


object UserControllerActor {

  val CHANNEL = "userChannel"

  case class SubscribeToUserAnnouncement(user: User)

  case class UnSubscribeFromUserAnnouncement(user: User)


  def apply() = {
    Props(new UserControllerActor())
  }
}



