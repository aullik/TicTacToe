package tictactoe.controllers

import framework.FrameworkSelector
import play.api.mvc._


/**
  */
object TicTacToeApplication {


  def index(request: Request[AnyContent], fws: FrameworkSelector): Result = {
    fws.index
  }


  def signUpPage(request: Request[AnyContent], fws: FrameworkSelector): Result = {
    fws.signUpPage
  }

}
