package actor

import actor.WebSocketActor._
import akka.actor._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator.{Publish, Subscribe}
import controllers.User
import grizzled.slf4j.Logging
import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization


/**
  */
class WebSocketActor(out: ActorRef, user: User) extends Actor with Logging {


  implicit val formats = Serialization.formats(NoTypeHints)
  val mediator = DistributedPubSub(context.system).mediator

  mediator ! Subscribe(CHANNEL, self)
  mediator ! Publish(CHANNEL, LoggedInAnnouncement(self, user))

  object ExtendedFunction extends PartialFunction[Any, Unit] {
    private val pf: PartialFunction[Any, Unit] = {
      case msg: String => handleMsg(msg)
      //      case refreshRequest: RefreshRequest => handleRefreshRequest(refreshRequest)
      case startWorkAnnouncement: LoggedInAnnouncement => handleStartWorkAnnouncement(startWorkAnnouncement)
      case stopWorkAnnouncement: LoggedOutAnnouncement => handleStopWorkAnnouncement(stopWorkAnnouncement)
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
    val jsonValue = parse(msg)
    try {
      (jsonValue \ "msgType").values match {
        case _ => throw new IllegalArgumentException("Invalid message")
      }
      //      mediator ! Publish(teamId, new RefreshRequest(userId, jsonValue \ "page"))
      //      sendSaveAckToClient((jsonValue \ "page").extract[String], success = true, "")
    } catch {
      case e: Exception =>
        info(e)
      //        sendSaveAckToClient((jsonValue \ "page").extract[String], success = false, e.getMessage)
    }
  }


  //  def handleRefreshRequest(request: RefreshRequest): Unit = {
  //    if (!request.sender.equals(userId)) {
  //      val outMsg = passRefreshRequestToClient(request.page.extract[String])
  //      out ! outMsg
  //    }
  //  }

  def handleStartWorkAnnouncement(loggedInAnnouncement: LoggedInAnnouncement): Unit = {
    if (sender() == self)
      return

    out ! compact(render(("msgType" -> "LoggedInAnnouncement") ~ ("user" -> loggedInAnnouncement.user.name)))

  }

  def handleStopWorkAnnouncement(stopWorkAnnouncement: LoggedOutAnnouncement): Unit = {
    if (sender() == self)
      return

    out ! compact(render(("msgType" -> "StopWorkAnnouncement") ~ ("user" -> stopWorkAnnouncement.user.name)))
  }


  //  def passRefreshRequestToClient(pageToRefresh: String): String = {
  //    compact(render(("msgType" -> "refreshRequest") ~ ("page" -> pageToRefresh)))
  //  }

  def sendStopWorkAnnouncementToClient(userName: String): Unit = {
  }

  override def postStop() = {
    mediator ! Publish(CHANNEL, LoggedOutAnnouncement(user))
  }
}

object WebSocketActor {

  val CHANNEL = "userChannel"

  case class LoggedInAnnouncement(sender: ActorRef, user: User)

  //  case class RefreshRequest(sender: String, page: JValue)

  case class LoggedOutAnnouncement(user: User)


  def apply(out: ActorRef, usr: User) = {
    Props(new WebSocketActor(out, usr))
  }
}