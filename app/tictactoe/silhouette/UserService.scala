package tictactoe.silhouette

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import tictactoe.controllers.webControllers.UserController
import grizzled.slf4j.Logging
import models.User
import tictactoe.exceptions.PersistenceException.EntityNotFoundException
import tictactoe.model.User

import scala.concurrent.Future

/** Provides the means to retrieve identities for the Silhouette module
  *
  */
class UserService @Inject()(userController: UserController) extends IdentityService[User] with Logging {

  /** Retrieves an identity that matches the specified login info
    *
    * @param loginInfo The login info (email) to retrieve an identity.
    * @return The retrieved identity (player) or None if no identity could be retrieved for the given login info.
    */
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = {
    info(s"retrieve(${loginInfo.providerKey})")
    try {
      val player = userController.getUserByEmail(loginInfo.providerKey)
      Future.successful(Option(player.get))
    } catch {
      case e: EntityNotFoundException => Future.successful(None)
    }
  }

  def save(user: User): Future[User] = {
    userController
    .
  }
}
