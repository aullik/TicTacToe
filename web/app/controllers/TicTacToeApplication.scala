package controllers

import play.api.mvc.Results._
import play.api.mvc._
import tictactoe.model.User


/**
  */
object TicTacToeApplication {


  def index(user: User, request: Request[AnyContent]): Result = {
    Ok(views.html.polymer.index())
  }


  def signUpPage(request: Request[AnyContent]): Result = {
    Ok(views.html.polymer.index())
  }

}
