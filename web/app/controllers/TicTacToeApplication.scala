package controllers

import play.api.mvc.Results._
import play.api.mvc._


/**
  */
object TicTacToeApplication {


  def index(user: User, request: Request[AnyContent]): Result = {
    val list = UserController.getAllActiveUserNames.filterNot(_ == user.name)
    Ok(bootstrap.views.html.index(list))
  }


  def signUpPage(request: Request[AnyContent]): Result = {
    Ok(bootstrap.views.html.signup())
  }

}
