package controllers

import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.controller.IController
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{AnyContent, Request, Result}

import scala.collection.mutable


/**
  */
object GameController {

  private val cacheUserName2Game = mutable.Map.empty[String, TicTacToe]

  /**
    * Start the game internally.
    *
    * @param request
    * @param otherPlayer
    * @return
    */
  def startGame(user: User, request: Request[AnyContent], otherPlayer: String): Result = {
    if (cacheUserName2Game.get(otherPlayer).isDefined || cacheUserName2Game.get(user.name).isDefined)
      BadRequest("User already in game")

    val tictactoe = new TicTacToe()
    tictactoe.getController.setPlayers(user.name, otherPlayer)
    cacheUserName2Game.put(user.name, tictactoe)
    cacheUserName2Game.put(otherPlayer, tictactoe)
    Ok("Game started")
  }

  /**
    * connect to already started game. If no game is started connect to index
    *
    * @param request
    * @return
    */
  def game(user: User, request: Request[AnyContent]): Result = {
    val gameopt = cacheUserName2Game.get(user.name)

    gameopt.map(game => {
      Ok(views.html.bootstrap.tictactoe(game.getController.getStatus, user.name))
    }).getOrElse(BadRequest("No game for user"))
  }


  def move(user: User, data: String, request: Request[AnyContent]): Result = {
    val controller: IController = null
    // TODO: you receive data as a String (grid-column-row) set a move and check if there is a win
    // TODO: if true return status and win with 1 else return status with win with 0

    //TODO: this code is to improve
    val list = data.split("-").map(_.toInt)
    controller.setValue(list(2), list(1), list(0))
    var returnedData = Map(
      "status" -> controller.getStatus,
      "win" -> "1"
    )
    if (!controller.getWin(0) && !controller.getWin(1)) {
      returnedData = Map(
        "status" -> controller.getStatus,
        "win" -> "0"
      )
    }
    val json = Json.toJson(returnedData)
    val jsonString: String = Json.stringify(json)
    Ok(jsonString)

  }


}
