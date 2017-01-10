package viewModel

import play.api.libs.functional.syntax._
import play.api.libs.json.Format
import viewModel.ViewModel.Path

/**
  */
case class LoginData(email: String, password: String)


object LoginData {
  implicit val format: Format[LoginData] = (
    Path[String]("email") and
      Path[String]("password")
    ).apply(LoginData.apply, unlift(LoginData.unapply))
}
