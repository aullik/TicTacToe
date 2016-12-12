package controllers

import play.api.mvc.Results._
import play.api.mvc._


class Application extends Controller {

  def index = LoggedInAction(TicTacToeApplication.index)

  def startGame(other: String) = LoggedInAction(GameController.startGame(_, _, other))

  def game = LoggedInAction(GameController.game)

  def signupPage = LoggedOutAction(TicTacToeApplication.signupPage)

  def signup = Action(UserController.signup _)

  def login = Action(UserController.login _)

  def move(data: String) = LoggedInAction(GameController.move(_, data, _))


}

/**
  * For all Actions That should be done while logged in.
  */
private object LoggedInAction {
  def apply(block: (User, Request[AnyContent]) => Result): Action[AnyContent] = {
    Action(request => checkRequest(request, block))
  }

  private def checkRequest(request: Request[AnyContent], block: (User, Request[AnyContent]) => Result): Result = {
    val opt: Option[User] = UserController.getUserFromToken(request.session)
    opt.map(usr => block(usr, request)).getOrElse(Redirect(routes.Application.signupPage()))
  }
}

/**
  * For all Actions That should be done while logged out.
  */
private object LoggedOutAction {

  def apply(block: Request[AnyContent] => Result): Action[AnyContent] =
    Action(request => UserController.getUserFromToken(request.session)
      .map(_ => Redirect(routes.Application.index())).getOrElse(block(request)))


}

