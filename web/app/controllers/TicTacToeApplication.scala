package controllers

import play.api.mvc.Results._
import play.api.mvc._


/**
  */
object TicTacToeApplication {


  def index(user: User, request: Request[AnyContent]): Result = {
    Ok(polymer.views.html.index())
  }


  def signUpPage(request: Request[AnyContent]): Result = {
    Ok(polymer.views.html.index())
  }

}
