package controllers

import javax.inject.Inject

import com.google.inject.Provider
import com.mohiva.play.silhouette.api.Silhouette
import framework.FrameworkSelector
import tictactoe.authentication.{AuthenticatedAction, BasicAction, UnAuthenticatedAction}
import tictactoe.controllers.webControllers.{AuthController, WebController, WebControllerContainer}
import play.api.i18n.MessagesApi
import play.api.mvc._
import tictactoe.silhouette.TicTacToeEnv
import tictactoe.controllers.TicTacToeApplication


class ScalaRoutes @Inject()(val messagesApi: MessagesApi,
                            val silhouette: Silhouette[TicTacToeEnv],
                            val webProvider: Provider[WebControllerContainer]
                           ) extends AuthController with WebController {

  def index = AuthenticatedGet(TicTacToeApplication.index _)

  def game = AuthenticatedGet(GameController.game _)

  def signUpPage = UnAuthenticatedGet(TicTacToeApplication.signUpPage _)

  def signUp = UnAuthenticatedPost(Auth.startSignUp _)

  def signUpEmail(token: String) = UnAuthenticatedGet(Auth.signUpEmail(token) _)

  def login = UnAuthenticatedPost(Auth.authenticate _)

  def oAuth(provider: String) = UnAuthenticatedPost(SocialAuthController.authenticate(provider) _)

  def logout = AuthenticatedGet(Auth.signOut _)

  def selectFramework(framework: String) = BasicPost(FrameworkSelector.selectFramework(framework) _)

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

  private object BasicPost extends BasicAction(messagesApi, silhouette)

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

