package tictactoe.controllers.webControllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import framework.FrameworkSelector
import play.api.mvc.{AnyContent, Result}
import tictactoe.silhouette.TicTacToeEnv


/**
  */
object GameController {

  /**
    * connect to already started game. If no game is started connect to index
    *
    * @param request
    * @return
    */
  def game(request: SecuredRequest[TicTacToeEnv, AnyContent], fws: FrameworkSelector): Result = {
    fws.game
  }


}
