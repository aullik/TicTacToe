package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import controllers.authentication.{AuthenticatedAction, BasicAction, UnAuthenticatedAction}
import play.api.i18n.MessagesApi
import play.api.mvc._
import silhouette.TicTacToeEnv


class Application @Inject()(val messagesApi: MessagesApi, val silhouette: Silhouette[TicTacToeEnv]) extends AuthController {

  def index = AuthenticatedGet(TicTacToeApplication.index _)

  def game = AuthenticatedGet(GameController.game _)

  def signUpPage = UnAuthenticatedGet(TicTacToeApplication.signUpPage _)

  def signUp = UnAuthenticatedPost(Auth.startSignUp _)

  def signUpEmail(token: String) = UnAuthenticatedPost(Auth.signUpEmail(token) _)

  def login = UnAuthenticatedPost(UserController.login _)

  def logout = AuthenticatedGet(UserController.logout _)

  def selectFramework(framework: String) = {
    //TODO:  Framworks can be :
    /*polymerjs
      angular2js
      bootstrap*/
    TODO
  }


  def polymer(element: String) = Action(getPolymer(element) _)

  def getPolymer(element: String)(request: Request[AnyContent]): Result = {
    try {
      val elementClass = Class.forName("views.html.polymer." + element)
      val htmlRender = elementClass.getMethod("render")
      val ret = htmlRender.invoke(null).toString
      Ok(ret)
    } catch {
      case e: ClassNotFoundException => BadRequest("Class not found")
      case e: NoSuchMethodException => BadRequest("NoSuchMethodException")
      case e: Exception => BadRequest
    }
  }


  private object AuthenticatedGet extends AuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedGet extends UnAuthenticatedAction(messagesApi, silhouette)

  private object AuthenticatedPost extends AuthenticatedAction(messagesApi, silhouette)

  private object UnAuthenticatedPost extends UnAuthenticatedAction(messagesApi, silhouette)

  private object BasicGet extends BasicAction(messagesApi, silhouette)

  //maybe needed in the future
  // private object BasicPost extends BasicAction(messagesApi, silhouette)

}


///**
//  * For all Actions That should be done while logged in.
//  */
//private object LoggedInAction {
//  def apply(block: (User, Request[AnyContent]) => Result): Action[AnyContent] = {
//    Action(request => UserController.getUserFromToken(request.session) match {
//      case Some(usr) => block(usr, request)
//      case None => Redirect(routes.Application.signUpPage())
//    })
//  }
//
//}
//
//private object UnAuthenticatedGet extends play.api.mvc.Results {
//
//  def apply(messagesApi: MessagesApi, block: (Request[AnyContent]) => Result): Action[AnyContent] = apply(messagesApi, (r, m) => block(r))
//
//  def apply(messagesApi: MessagesApi, block: (Request[AnyContent], Messages) => Result): Action[AnyContent] = {
//    Action(request => {
//      block(request, messagesApi.preferred(request))
//    })
//  }
//}
//
///**
//  * For all Actions That should be done while logged out.
//  */
//private object LoggedOutAction {
//
//
//  def apply(block: Request[AnyContent] => Result): Action[AnyContent] =
//    Action(request => UserController.getUserFromToken(request.session) match {
//      case Some(_) => Redirect(routes.Application.index())
//      case None => block(request)
//    })
//
//
//}

