package controllers.authentication

import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Results => HTMLResults, _}
import silhouette.TicTacToeEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  */
private[controllers] abstract class AbstractSilhouetteAction[REQ <: Request[AnyContent]](messagesApi: MessagesApi, silhouette: Silhouette[TicTacToeEnv]
                                                                                        ) extends AbstractAction[REQ](messagesApi, silhouette) {


  protected[authentication] def executeCheckedHandlerResult(block: () => Future[Result],
                                                            ec: ExecutionContext): Future[HandlerResult[Nothing]] = {
    executeChecked(block).map(HandlerResult[Nothing](_))(ec)
  }

  protected[authentication] def handleSilhouetteRequest(block: (REQ, () => Messages) => Future[Result],
                                                        ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]]

  override protected[authentication] def handleFutureRequest(block: (REQ, () => Messages) => Future[Result],
                                                             ec: ExecutionContext)
                                                            (request: Request[AnyContent]): Future[Result] = {
    handleSilhouetteRequest(block, ec)(request).map(_.result)(ec)
  }

}
