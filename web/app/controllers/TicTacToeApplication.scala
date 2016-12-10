package controllers

import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.controller.IController
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._


/**
  */
object TicTacToeApplication {
  var controller: IController = null


  def index(user: User, request: Request[AnyContent]): Result = {

    //TODO: the list of all users (username) must be send to the view
    val list = UserController.getAllActiveUserNames
    Ok(bootstrap.views.html.index(list))
  }

  /**
    * Start the game internally.
    *
    * @param request
    * @param player1
    * @param player2
    * @return
    */
  def startGame(user: User, request: Request[AnyContent], player1: String, player2: String): Result = {
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


  def signupPage(request: Request[AnyContent]): Result = {
    request.session.data.foreach(println _)

    //TODO: check if there is a cookie for user data if true to to index else go to signup
    Ok(bootstrap.views.html.signup())
  }

  def signup(request: Request[AnyContent]): Result = {
    //TODO: save user in the users list and create an new user object
    Ok("ok")
  }

  def login(request: Request[AnyContent]): Result = {
    val json = request.body.asJson.get
    val changedSession = UserController.login(request.session)
    //TODO: check if the data is from a current user if true go to the index page else return with errors.
    Ok.withSession(changedSession)
  }

  def move(user: User, data: String, request: Request[AnyContent]): Result = {

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
    request =>


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
