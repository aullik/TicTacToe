package controllers

import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.controller.IController
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc.{Action, AnyContent, Request, Result}


/**
  */
object GameController {


  /**
    * Start the game internally.
    *
    * @param request
    * @param otherPlayer
    * @return
    */
  def startGame(user: User, request: Request[AnyContent], otherPlayer: String): Result = {
    null
  }

  /**
    * connect to already started game. If no game is started connect to index
    *
    * @param request
    * @return
    */
  def game(user: User, request: Request[AnyContent]): Result = {
    null
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

  //REMOVE
  def call(caller: String) = Action {

    var controller: IController = null

    //TODO: this is the method when a user want to play with another one
    //TODO: here will be the initialization on a controller and adding it the the list and setPlayers method
    //TODO: will be called with the user names

    //this code has to be improved
    if (caller.equals("1")) {
      val tictactoe = new TicTacToe()
      controller = tictactoe.getController
      controller.setPlayers("coco", "bobo")
    }
    Ok(bootstrap.views.html.tictactoe(controller.getStatus, caller))
  }


}
