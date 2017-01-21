package tictactoe.authentication

import com.mohiva.play.silhouette.api.Silhouette
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc._
import tictactoe.silhouette.TicTacToeEnv

import scala.concurrent.{ExecutionContext, Future}


/**
  */
private[controllers] class BasicAction(messagesApi: MessagesApi, silhouette: Silhouette[TicTacToeEnv]
                                      ) extends AbstractAction[Request[AnyContent]](messagesApi, silhouette) {


  override protected[authentication] def handleFutureRequest(block: (Request[AnyContent], () => Messages) => Future[Result],
                                                             ec: ExecutionContext)
                                                            (request: Request[AnyContent]): Future[Result] = {
    executeChecked(() => block(request, () => getMessages(request)))
  }

}
