package controllers.authentication

import com.mohiva.play.silhouette.api.Silhouette
import grizzled.slf4j.Logging
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Results => HTMLResults, _}
import silhouette.TicTacToeEnv
import tictactoe.persistence.CheckLocks

import scala.concurrent.{ExecutionContext, Future}


/**
  */
private[controllers] abstract class AbstractAction[REQ <: Request[AnyContent]](messagesApi: MessagesApi, silhouette: Silhouette[TicTacToeEnv]) extends HTMLResults with Logging {


  protected[authentication] def executeChecked[R](block: () => R): R = {
    try {
      block()
    } catch {
      case t: Throwable => error("Exception in action: " + t)
        throw t
    } finally {
      CheckLocks.check()
    }
  }

  protected[authentication] def handleFutureRequest(block: (REQ) => Future[Result],
                                                    ec: ExecutionContext)
                                                   (request: Request[AnyContent]): Future[Result]

  protected[authentication] def doAction(block: (REQ) => Result): Action[AnyContent] =
    doActionFuture((r) => Future.successful(block(r)))


  protected[authentication] def doActionFuture(block: (REQ) => Future[Result]): Action[AnyContent] =
    Async.async(handleFutureRequest(block, Async.executionContext) _)


  implicit class ResultFunction(val fnc: () => Result) {}

  def apply(block: ResultFunction): Action[AnyContent] = doAction((r) => block.fnc())

  implicit class RequestFunction(val fnc: (REQ) => Result) {}

  def apply(block: RequestFunction): Action[AnyContent] = doAction((r) => block.fnc(r))

  implicit class MessageFunction(val fnc: (REQ, Messages) => Result) {}

  def apply(block: MessageFunction): Action[AnyContent] = doAction((r) => block.fnc(r, MSG(r)))


  implicit class FutureResultFunction(val fnc: () => Future[Result]) {}

  def apply(block: FutureResultFunction): Action[AnyContent] = doActionFuture((r) => block.fnc())

  implicit class FutureRequestFunction(val fnc: (REQ) => Future[Result]) {}

  def apply(block: FutureRequestFunction): Action[AnyContent] = doActionFuture((r) => block.fnc(r))

  implicit class FutureMessageFunction(val fnc: (REQ, Messages) => Future[Result]) {}

  def apply(block: FutureMessageFunction): Action[AnyContent] = doActionFuture((r) => block.fnc(r, MSG(r)))


  implicit class FutureECFunction(val fnc: (ExecutionContext) => Future[Result]) {}

  def apply(block: FutureECFunction): Action[AnyContent] = doActionFuture((r) => block.fnc(Async.executionContext))

  implicit class FutureRequestECFunction(val fnc: (REQ, ExecutionContext) => Future[Result]) {}

  def apply(block: FutureRequestECFunction): Action[AnyContent] = doActionFuture((r) => block.fnc(r, Async.executionContext))

  implicit class FutureMessageECFunction(val fnc: (REQ, Messages, ExecutionContext) => Future[Result]) {}

  def apply(block: FutureMessageECFunction): Action[AnyContent] = doActionFuture((r) => block.fnc(r, MSG(r), Async.executionContext))


  protected object Async extends ActionBuilder[Request] {
    override def executionContext: ExecutionContext = super.executionContext

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)
  }

  private object MSG {
    def apply(r: Request[AnyContent]) = messagesApi.preferred(r)
  }

}

