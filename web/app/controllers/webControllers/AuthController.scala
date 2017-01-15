package controllers.webControllers

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import grizzled.slf4j.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.Controller
import silhouette.TicTacToeEnv

/** A wrapper for play Controller with controllers.authentication Info
  *
  */
trait AuthController extends Controller with I18nSupport with Logging {
  def silhouette: Silhouette[TicTacToeEnv]

  def env: Environment[TicTacToeEnv] = silhouette.env
}
