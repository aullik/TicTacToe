package controllers

import play.api.mvc._


class Application extends Controller {

  def index = Action(TicTacToeApplication.index _)

  def startGame(player1: String, player2: String) = Action(TicTacToeApplication.startGame(_, player1, player2))

  def game = Action(TicTacToeApplication.game _)

  def signupPage = Action(TicTacToeApplication.signupPage _)

  def signup = Action(TicTacToeApplication.signup _)

  def login = Action(TicTacToeApplication.login _)

  def move(data: String) = Action(TicTacToeApplication.move(data, _))


}

