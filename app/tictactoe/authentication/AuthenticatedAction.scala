package tictactoe.authentication

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{AnyContent, Request, Result}
import tictactoe.silhouette.TicTacToeEnv

import scala.concurrent.{ExecutionContext, Future}

/**
  */
class AuthenticatedAction(messagesApi: MessagesApi, silhouette: Silhouette[TicTacToeEnv]
                                              ) extends AbstractSilhouetteAction[SecuredRequest[TicTacToeEnv, AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleSilhouetteRequest(block: (SecuredRequest[TicTacToeEnv, AnyContent], () => Messages) => Future[Result],
                                                                 ec: ExecutionContext): (Request[AnyContent]) => Future[HandlerResult[Nothing]] = {
    (request: Request[AnyContent]) => silhouette.SecuredRequestHandler(request)((req: SecuredRequest[TicTacToeEnv, AnyContent]) => {
      executeCheckedHandlerResult(() => block(req, () => getMessages(req)), ec)
    })
  }
}
