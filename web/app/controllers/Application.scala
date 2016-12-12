package controllers

import play.api.mvc.Results._
import play.api.mvc._


class Application extends Controller {

  def index = LoggedInAction(TicTacToeApplication.index)

  def startGame(other: String) = LoggedInAction(TicTacToeApplication.startGame(_, _, other))

  def game = LoggedInAction(TicTacToeApplication.game)

  def signupPage = LoggedOutAction(TicTacToeApplication.signupPage)

  def signup = Action(TicTacToeApplication.signup _)

  def login = Action(TicTacToeApplication.login _)

  def move(data: String) = LoggedInAction(TicTacToeApplication.move(_, data, _))


}

private object LoggedInAction {
  def apply(block: (User, Request[AnyContent]) => Result): Action[AnyContent] = {
    Action(request => checkRequest(request, block))
  }

  private def checkRequest(request: Request[AnyContent], block: (User, Request[AnyContent]) => Result): Result = {
    val opt: Option[User] = UserController.getUserFromToken(request.session)
    opt.map(usr => block(usr, request)).getOrElse(Redirect(routes.Application.signupPage()))
  }
}

private object LoggedOutAction {

  def apply(block: Request[AnyContent] => Result): Action[AnyContent] =
    Action(request => {
      val opt: Option[User] = UserController.getUserFromToken(request.session)
      if (opt.isDefined)
        Redirect(routes.Application.index())
      else
        block(request)
    })


}

