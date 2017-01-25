package tictactoe.controllers.webControllers


import java.util.concurrent.TimeUnit

import akka.pattern.ask
import com.google.inject.Inject
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import framework.FrameworkSelector
import play.api.mvc.{AnyContent, Result}
import tictactoe.TicTacToeServer
import tictactoe.actor.user.{LobbyActor, UserTokenManagerActor}
import tictactoe.model.User
import tictactoe.silhouette.TicTacToeEnv
import javax.inject.Singleton

import akka.actor.ActorSystem
import akka.util.Timeout

import scala.concurrent.{ExecutionContextExecutor, Future}

/**
  */
@Singleton
class GameController @Inject()(server: TicTacToeServer, system: ActorSystem) {
  implicit val exc: ExecutionContextExecutor = system.dispatcher

  /**
    * connect to already started game. If no game is started connect to index
    *
    * @param request
    * @return
    */
  def game(request: SecuredRequest[TicTacToeEnv, AnyContent], fws: FrameworkSelector): Future[Result] = {
    checkInGame(request.identity).map(if (_) fws.game else fws.index)
  }

  def checkInGame(user: User): Future[Boolean] = {
    server.userTokenManagerActor.?(UserTokenManagerActor.RequestUserHandlerForEmail(user.email))(Timeout(10, TimeUnit.SECONDS))
      .mapTo[UserTokenManagerActor.UserHandlerIfPresent].flatMap(uhOpt => {
      uhOpt.handlerOpt.map(uhc => {
        server.lobbyActor.?(LobbyActor.AskPlayerInGame(uhc.token))(Timeout(10, TimeUnit.SECONDS))
          .mapTo[LobbyActor.ReturnPlayerInGame].map(_.inGame)
      }).getOrElse(Future.successful(false))
    })
  }


}
