package tictactoe.viewModel

import play.api.libs.json.Json


/**
  */
case class SignUpData(email: String,
                      name: String,
                      password: String,
                      passwordRepetition: String,
                      rememberMe: Boolean)

object SignUpData {
  implicit val signUpForm = Json.format[SignUpData]
}
