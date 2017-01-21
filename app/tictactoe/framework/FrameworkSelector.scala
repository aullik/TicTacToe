package framework

import play.api.mvc._

/**
  */
sealed trait FrameworkSelector {
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
  override def index: Result = ???

  override def game: Result = ???

  override def signUpPage: Result = ???
}

object AngularSelector extends FrameworkSelector {
  override def index: Result = ???

  override def game: Result = ???

  override def signUpPage: Result = ???
}


object FrameworkSelector {

  private final val default = PlaySelector

  def getFramework(request: Request[AnyContent]): FrameworkSelector = {
    default
  }


}
