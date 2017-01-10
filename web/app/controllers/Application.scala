package controllers

import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc._


class Application extends Controller {

  def index = LoggedInAction(TicTacToeApplication.index)

  def startGame(other: String) = LoggedInAction(GameController.startGame(_, _, other))

  def game = LoggedInAction(GameController.game)

  def signUpPage = LoggedOutAction(TicTacToeApplication.signUpPage)

  def signUp = Action(UserController.signUp _)

  def login = Action(UserController.login _)

  def move(data: String) = LoggedInAction(GameController.move(_, data, _))

  def logout = LoggedInAction(UserController.logout)

  def getUsers = LoggedInAction(UserController.getUsers)

  def selectFramework(framework: String) = {
    //TODO:  Framworks can be :
    /*polymerjs
      angular2js
      bootstrap*/
  }

  def polymer(element: String)(request: Request[AnyContent]): Result = {
    try {
      val elementClass = Class.forName("polymer.views.html." + element)
      val htmlRender = elementClass.getMethod("render")
      val ret = htmlRender.invoke(null).toString
      Ok(ret)
    } catch {
      case e: ClassNotFoundException => BadRequest("Class not found")
      case e: NoSuchMethodException => BadRequest("NoSuchMethodException")
      case e: Exception => BadRequest
    }
  }
}

/**
  * For all Actions That should be done while logged in.
  */
private object LoggedInAction {
  def apply(block: (User, Request[AnyContent]) => Result): Action[AnyContent] = {
    Action(request => UserController.getUserFromToken(request.session) match {
      case Some(usr) => block(usr, request)
      case None => Redirect(routes.Application.signUpPage())
    })
  }

}

private object UnAuthenticatedGet extends play.api.mvc.Results {

  def apply(messagesApi: MessagesApi, block: (Request[AnyContent]) => Result): Action[AnyContent] = apply(messagesApi, (r, m) => block(r))

  def apply(messagesApi: MessagesApi, block: (Request[AnyContent], Messages) => Result): Action[AnyContent] = {
    Action(request => {
      block(request, messagesApi.preferred(request))
    })
  }
}

/**
  * For all Actions That should be done while logged out.
  */
private object LoggedOutAction {


  def apply(block: Request[AnyContent] => Result): Action[AnyContent] =
    Action(request => UserController.getUserFromToken(request.session) match {
      case Some(_) => Redirect(routes.Application.index())
      case None => block(request)
    })


}

