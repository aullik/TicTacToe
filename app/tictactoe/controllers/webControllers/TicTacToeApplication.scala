package tictactoe.controllers.webControllers

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import framework.FrameworkSelector
import play.api.mvc._
import tictactoe.silhouette.TicTacToeEnv
import javax.inject.Singleton

import akka.actor.ActorSystem

import scala.concurrent.{ExecutionContextExecutor, Future}


/**
  */
@Singleton
class TicTacToeApplication @Inject()(gameController: GameController, system: ActorSystem) {
  implicit val exc: ExecutionContextExecutor = system.dispatcher


  def index(request: SecuredRequest[TicTacToeEnv, AnyContent], fws: FrameworkSelector): Future[Result] = {
    gameController.checkInGame(request.identity).map(if (_) fws.game else fws.index)
  }


  def signUpPage(request: Request[AnyContent], fws: FrameworkSelector): Result = {
    fws.signUpPage
  }

}
