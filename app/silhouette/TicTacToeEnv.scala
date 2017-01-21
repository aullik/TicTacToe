package silhouette

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import tictactoe.model.User

/**
  * The default env.
  */
trait TicTacToeEnv extends Env {
  type I = User
  type A = CookieAuthenticator
}