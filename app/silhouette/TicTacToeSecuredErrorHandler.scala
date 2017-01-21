package silhouette

import javax.inject.{Inject, Provider, Singleton}

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import controllers.routes
import play.api._
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.Results._
import play.api.mvc.{RequestHeader, Result}
import play.api.routing.Router

import scala.concurrent.Future

@Singleton
class TicTacToeSecuredErrorHandler @Inject()(
                                              env: Environment,
                                              config: Configuration,
                                              sourceMapper: OptionalSourceMapper,
                                              router: Provider[Router],
                                              val messagesApi: MessagesApi
                                            ) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with SecuredErrorHandler with I18nSupport {

  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] = Future.successful {
    Redirect(routes.ScalaRoutes.signUpPage())
  }

  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] = Future.successful {
    Forbidden
  }
}
