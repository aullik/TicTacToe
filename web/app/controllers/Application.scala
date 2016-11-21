package controllers

import de.htwg.tictactoe.TicTacToe
import de.htwg.tictactoe.controller.IController
import play.api.libs.json.Json
import play.api.mvc._

class Application extends Controller {

  var controller: IController = null;

  def index = Action {
    val list = List("ysf", "nicolas", "dany", "ysf", "nicolas", "dany", "ysf", "nicolas", "dany", "ysf", "nicolas", "dany")
    Ok(bootstrap.views.html.index(list))
  }

  /*
    def tictactoe = Action {
      // val controller = tictactoe.getController();
      Ok(bootstrap.views.html.tictactoe(""))
    }*/

  def call(caller: String) = Action {
    if (caller.equals("1")) {
      val tictactoe = new TicTacToe()
      controller = tictactoe.getController()
      controller.setPlayers("coco", "bobo");
    }
    Ok(bootstrap.views.html.tictactoe(controller.getStatus(), caller))
  }

  def signup = Action {
    Ok(bootstrap.views.html.signup())
  }

  def move(data: String) = Action {
    val list = data.split("-").map(_.toInt);
    controller.setValue(list(2), list(1), list(0))
    var returnedData = Map(
      "status" -> controller.getStatus(),
      "win" -> "1"
    )
    if (!controller.getWin(0) && !controller.getWin(1)) {
      returnedData = Map(
        "status" -> controller.getStatus(),
        "win" -> "0"
      )
    }


    var json = Json.toJson(returnedData)
    val jsonString: String = Json.stringify(json)
    Ok(jsonString);

  }

}