package controllers

import javax.inject.Inject

import actor.WebSocketActor
import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Flow
import com.google.inject.Provider
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import controllers.webControllers.{WebController, WebControllerContainer}
import grizzled.slf4j.Logging
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import silhouette.TicTacToeEnv

import scala.concurrent.ExecutionContext.Implicits.{global => executionContext}
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  */
class WebSocketsControl @Inject()(implicit system: ActorSystem,
                                  val silhouette: Silhouette[TicTacToeEnv],
                                  val mat: Materializer,
                                  val webProvider: Provider[WebControllerContainer]
                                 ) extends WebController with Controller with Logging {

  private def getFlow(securedRequest: SecuredRequest[TicTacToeEnv, AnyContent]): HandlerResult[Flow[String, String, _]] = {
    Try(ActorFlow.actorRef(WebSocketActor(_, securedRequest.identity))) match {
      case Success(flow) =>
        HandlerResult(Ok, Some(flow))

      case Failure(e) =>
        warn(e)
        HandlerResult(WebSocketsControl.onFailure, None)
    }
  }

  private def silhouetteFlow(request: Request[AnyContent]): Future[Either[Result, Flow[String, String, _]]] = {
    silhouette.SecuredRequestHandler(securedRequest => {
      Future.successful(getFlow(securedRequest))
    })(request).map {
      case HandlerResult(r, Some(flow)) => Right(flow)
      case HandlerResult(r, None) =>
        r match {
          case WebSocketsControl.onFailure =>
          case any => info(s"silhouette returned result: $any")
        }
        Left(WebSocketsControl.onFailure)
    }(executionContext)
  }

  def socket(): WebSocket = {
    WebSocket.acceptOrResult[String, String](requestHeader => {
      val request = Request(requestHeader, AnyContentAsEmpty)
      silhouetteFlow(request)
    })
  }

}

object WebSocketsControl extends Controller {
  private[WebSocketsControl] val onFailure = InternalServerError
}
