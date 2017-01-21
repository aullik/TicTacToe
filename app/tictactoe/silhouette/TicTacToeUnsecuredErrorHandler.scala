package tictactoe.silhouette

import javax.inject.{Inject, Provider, Singleton}

import com.mohiva.play.silhouette.api.actions.UnsecuredErrorHandler
import controllers.routes
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.Future

@Singleton
class TicTacToeUnsecuredErrorHandler @Inject()(
                                                env: Environment,
                                                config: Configuration,
                                                sourceMapper: OptionalSourceMapper,
                                                router: Provider[Router],
                                                val messagesApi: MessagesApi
                                              ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with UnsecuredErrorHandler with I18nSupport {


  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    Redirect(routes.ScalaRoutes.index())
  }
}
