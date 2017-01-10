package viewModel

import play.api.libs.json.Format
import viewModel.ViewModel.Path
import play.api.libs.functional.syntax._


/**
  */
case class SignUpData(email: String, username: String, password: String)

object SignUpData {
  implicit val format: Format[SignUpData] = (
    Path[String]("email") and
      Path[String]("username") and
      Path[String]("password")
    ).apply(SignUpData.apply, unlift(SignUpData.unapply))
}
