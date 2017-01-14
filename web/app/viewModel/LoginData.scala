package viewModel

import play.api.libs.json.Json

/**
  */
case class LoginData(email: String,
                     password: String,
                     rememberMe: Boolean)


object LoginData {
  implicit val loginForm = Json.format[LoginData]

}
