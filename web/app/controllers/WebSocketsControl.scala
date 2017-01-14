package controllers

import javax.inject.Inject

import actor.WebSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.Future

/**
  */
class WebSocketsControl @Inject()(implicit system: ActorSystem, mat: Materializer) extends Controller {

  def socket(playerEmail: String): WebSocket =
    WebSocket.acceptOrResult[String, String](_ =>
      Future.successful(getFlow(playerEmail))
    )


  private def getFlow(playerEmail: String): Either[Result, Flow[String, String, _]] = {
    UserController.getUserFromEmail(playerEmail).map(usr => ActorFlow.actorRef(WebSocketActor(_, usr))) match {
      case Some(flow) => Right(flow)
      case None => Left(BadRequest("invalid email"))
    }
  }
}
