package controllers

import play.api.mvc.Results._
import play.api.mvc._


class Application extends Controller {

  def index = AuthAction(TicTacToeApplication.index)

  def startGame(player1: String, player2: String) = AuthAction(TicTacToeApplication.startGame(_, _, player1, player2))

  def game = AuthAction(TicTacToeApplication.game)

  def signupPage = AuthAction(TicTacToeApplication.index)

  def signup = Action(TicTacToeApplication.signup _)

  def login = Action(TicTacToeApplication.login _)

  def move(data: String) = AuthAction(TicTacToeApplication.move(_, data, _))


}

object AuthAction {
  def apply(block: (User, Request[AnyContent]) => Result): Action[AnyContent] = {
    Action(request => checkRequest(request, block))
  }

  private def checkRequest(request: Request[AnyContent], block: (User, Request[AnyContent]) => Result): Result = {
    val opt: Option[User] = UserController.getUserFromToken(request.session)
    opt.map(usr => block(usr, request)).getOrElse(Redirect(routes.Application.signupPage()))
  }
}

