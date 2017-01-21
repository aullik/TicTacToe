package framework

import play.api.mvc._

/**
  */
sealed trait FrameworkSelector extends Controller {
  def index: Result

  def game: Result

  def signUpPage: Result
}


object PlaySelector extends FrameworkSelector {
  override def index: Result = ???

  override def game: Result = ???

  override def signUpPage: Result = ???
}

object BootstrapSelector extends FrameworkSelector {
  override def index: Result = Ok(views.html.bootstrap.index())

  override def game: Result = Ok(views.html.bootstrap.tictactoe())

  override def signUpPage: Result = Ok(views.html.bootstrap.signup())

}

object AngularSelector extends FrameworkSelector {
  override def index: Result = ???

  override def game: Result = ???

  override def signUpPage: Result = ???
}


object FrameworkSelector extends Controller {

  private final val IDENT = "framework"

  private final val default = BootstrapSelector

  def getFramework(request: Request[AnyContent]): FrameworkSelector = {
    request.session.get(IDENT).flatMap {
      case "a" => Some(AngularSelector)
      case "p" => Some(PlaySelector)
      case "b" => Some(BootstrapSelector)
      case _ => None
    }.getOrElse(default)
  }

  def selectFramework(f: String)(request: Request[AnyContent]): Result = {
    Ok.withSession(request.session.copy(request.session.data.updated(IDENT, f)))
  }


}
