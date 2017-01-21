package tictactoe.authentication

import com.mohiva.play.silhouette.api.Silhouette
import framework.FrameworkSelector
import grizzled.slf4j.Logging
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Results => HTMLResults, _}
import tictactoe.persistence.CheckLocks
import tictactoe.silhouette.TicTacToeEnv

import scala.concurrent.{ExecutionContext, Future}


/**
  * Ordering or apply method parameters: Request, followed by everything else in alphabetical order
  *
  */
private[authentication] abstract class AbstractAction[REQ <: Request[AnyContent]](messagesApi: MessagesApi,
                                                                                  silhouette: Silhouette[TicTacToeEnv]
                                                                                 ) extends HTMLResults with Logging {


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


  /*
  ordering:
  REQ
  EXC
  FWS
  MSG
   */

  implicit class ResultFunction(val fnc: () => Result) {}

  def apply(block: ResultFunction): Action[AnyContent] = doAction((_) => block.fnc())

  implicit class REQ_Function(val fnc: (REQ) => Result) {}

  def apply(block: REQ_Function): Action[AnyContent] = doAction((r) => block.fnc(r))

  implicit class EXC_Function(val fnc: (EXC) => Result) {}

  def apply(block: EXC_Function): Action[AnyContent] = doAction((_) => block.fnc(EXC()))

  implicit class FWS_Function(val fnc: (FWS) => Result) {}

  def apply(block: FWS_Function): Action[AnyContent] = doAction((r) => block.fnc(FWS(r)))

  implicit class MSG_Function(val fnc: (MSG) => Result) {}

  def apply(block: MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(MSG(r)))

  implicit class REQ_EXC_Function(val fnc: (REQ, EXC) => Result) {}

  def apply(block: REQ_EXC_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC()))

  implicit class REQ_FSW_Function(val fnc: (REQ, FWS) => Result) {}

  def apply(block: REQ_FSW_Function): Action[AnyContent] = doAction((r) => block.fnc(r, FWS(r)))

  implicit class REQ_MSG_Function(val fnc: (REQ, MSG) => Result) {}

  def apply(block: REQ_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, MSG(r)))

  implicit class EXC_FWS_Function(val fnc: (EXC, FWS) => Result) {}

  def apply(block: EXC_FWS_Function): Action[AnyContent] = doAction((r) => block.fnc(EXC(), FWS(r)))

  implicit class EXC_MSG_Function(val fnc: (EXC, MSG) => Result) {}

  def apply(block: EXC_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(EXC(), MSG(r)))

  implicit class FWS_MSG_Function(val fnc: (FWS, MSG) => Result) {}

  def apply(block: FWS_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(FWS(r), MSG(r)))

  implicit class REQ_EXC_FWS_Function(val fnc: (REQ, EXC, FWS) => Result) {}

  def apply(block: REQ_EXC_FWS_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC(), FWS(r)))

  implicit class REQ_EXC_MSG_Function(val fnc: (REQ, EXC, MSG) => Result) {}

  def apply(block: REQ_EXC_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC(), MSG(r)))

  implicit class REQ_FWS_MSG_Function(val fnc: (REQ, FWS, MSG) => Result) {}

  def apply(block: REQ_FWS_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, FWS(r), MSG(r)))

  implicit class EXC_FWS_MSG_Function(val fnc: (EXC, FWS, MSG) => Result) {}

  def apply(block: EXC_FWS_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(EXC(), FWS(r), MSG(r)))

  implicit class REQ_EXC_FWS_MSG_Function(val fnc: (REQ, EXC, FWS, MSG) => Result) {}

  def apply(block: REQ_EXC_FWS_MSG_Function): Action[AnyContent] = doAction((r) => block.fnc(r, EXC(), FWS(r), MSG(r)))


  // future

  implicit class FutureResultFunction(val fnc: () => Future[Result]) {}

  def apply(block: FutureResultFunction): Action[AnyContent] = doActionFuture((_) => block.fnc())

  implicit class Future_REQ_Function(val fnc: (REQ) => Future[Result]) {}

  def apply(block: Future_REQ_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r))

  implicit class Future_EXC_Function(val fnc: (EXC) => Future[Result]) {}

  def apply(block: Future_EXC_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC()))

  implicit class Future_FWS_Function(val fnc: (FWS) => Future[Result]) {}

  def apply(block: Future_FWS_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(FWS(r)))

  implicit class Future_MSG_Function(val fnc: (MSG) => Future[Result]) {}

  def apply(block: Future_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(MSG(r)))

  implicit class Future_REQ_EXC_Function(val fnc: (REQ, EXC) => Future[Result]) {}

  def apply(block: Future_REQ_EXC_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC()))

  implicit class Future_REQ_FSW_Function(val fnc: (REQ, FWS) => Future[Result]) {}

  def apply(block: Future_REQ_FSW_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, FWS(r)))

  implicit class Future_REQ_MSG_Function(val fnc: (REQ, MSG) => Future[Result]) {}

  def apply(block: Future_REQ_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, MSG(r)))

  implicit class Future_EXC_FWS_Function(val fnc: (EXC, FWS) => Future[Result]) {}

  def apply(block: Future_EXC_FWS_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(), FWS(r)))

  implicit class Future_EXC_MSG_Function(val fnc: (EXC, MSG) => Future[Result]) {}

  def apply(block: Future_EXC_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(), MSG(r)))

  implicit class Future_FWS_MSG_Function(val fnc: (FWS, MSG) => Future[Result]) {}

  def apply(block: Future_FWS_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(FWS(r), MSG(r)))

  implicit class Future_REQ_EXC_FWS_Function(val fnc: (REQ, EXC, FWS) => Future[Result]) {}

  def apply(block: Future_REQ_EXC_FWS_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC(), FWS(r)))

  implicit class Future_REQ_EXC_MSG_Function(val fnc: (REQ, EXC, MSG) => Future[Result]) {}

  def apply(block: Future_REQ_EXC_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC(), MSG(r)))

  implicit class Future_REQ_FWS_MSG_Function(val fnc: (REQ, FWS, MSG) => Future[Result]) {}

  def apply(block: Future_REQ_FWS_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, FWS(r), MSG(r)))

  implicit class Future_EXC_FWS_MSG_Function(val fnc: (EXC, FWS, MSG) => Future[Result]) {}

  def apply(block: Future_EXC_FWS_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(EXC(), FWS(r), MSG(r)))

  implicit class Future_REQ_EXC_FWS_MSG_Function(val fnc: (REQ, EXC, FWS, MSG) => Future[Result]) {}

  def apply(block: Future_REQ_EXC_FWS_MSG_Function): Action[AnyContent] = doActionFuture((r) => block.fnc(r, EXC(), FWS(r), MSG(r)))


  protected object Async extends ActionBuilder[Request] {
    override def executionContext: ExecutionContext = super.executionContext

    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = block(request)
  }

  private type EXC = ExecutionContext

  private object EXC {
    def apply(): EXC = Async.executionContext
  }

  private type MSG = Messages

  private object MSG {
    def apply(r: Request[AnyContent]): MSG = messagesApi.preferred(r)
  }

  private type FWS = FrameworkSelector

  private object FWS {
    def apply(r: Request[AnyContent]): FWS = framework.FrameworkSelector.getFramework(r)
  }

}


