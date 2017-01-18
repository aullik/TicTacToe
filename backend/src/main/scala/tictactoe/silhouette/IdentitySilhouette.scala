package tictactoe.silhouette

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import tictactoe.silhouette.Implicits._

/**
  * Authenticated user in Silhouette
  */
trait IdentitySilhouette extends Identity {

  def key: String

  def loginInfo: LoginInfo = key2loginInfo(key)
}
