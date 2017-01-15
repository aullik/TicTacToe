package controllers

import javax.inject.Inject

import actor.WebSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.google.inject.Provider
import controllers.webControllers.{WebController, WebControllerContainer}
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.Future

/**
  */
class WebSocketsControl @Inject()(implicit system: ActorSystem,
                                  mat: Materializer,
                                  val webProvider: Provider[WebControllerContainer]
                                 ) extends WebController with Controller {

  def socket(): WebSocket =
    WebSocket.acceptOrResult[String, String](request =>
      Future.successful(getFlow(request.session))
    )


  private def getFlow(session: Session): Either[Result, Flow[String, String, _]] = {
    UserController.getUserFromToken(session).map(usr => ActorFlow.actorRef(WebSocketActor(_, usr))) match {
      case Some(flow) => Right(flow)
      case None => Left(BadRequest("invalid email"))
    }
  }


}
