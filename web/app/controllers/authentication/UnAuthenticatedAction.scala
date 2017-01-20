package controllers.authentication

import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Results => HTMLResults, _}
import silhouette.TicTacToeEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  */
private[controllers] class UnAuthenticatedAction(messagesApi: MessagesApi, silhouette: Silhouette[TicTacToeEnv]
                                                ) extends AbstractSilhouetteAction[Request[AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleSilhouetteRequest(block: (Request[AnyContent]) => Future[Result],
                                                                 ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]] = {
    (request: Request[AnyContent]) => silhouette.UnsecuredRequestHandler(request)(req => {
      executeCheckedHandlerResult(() => block(req), ec)
    })
  }

}
