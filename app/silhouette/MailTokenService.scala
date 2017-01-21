package silhouette

import grizzled.slf4j.Logging
import mailer.MailTokenUser

import scala.concurrent.ExecutionContext.Implicits.{global => executionContext}
import scala.concurrent.Future

/** Service to create, retrieve and consume tokens
  *
  * @tparam T MailToken
  */
trait MailTokenService[T <: MailToken] {

  /** Saves given token
    *
    * @param token token to save
    * @return given token or None
    */
  def create(token: T): Future[Option[T]]

  /** Finds given token
    *
    * @param id id of token
    * @return token with given id or none
    */
  def retrieve(id: String): Future[Option[T]]

  /** Deletes a token
    *
    * @param id of token
    */
  def consume(id: String): Unit
}

class MailTokenUserService extends MailTokenService[MailTokenUser] with Logging {
  override def create(token: MailTokenUser): Future[Option[MailTokenUser]] = {
    MailTokenUser.save(token).map(Some(_))(executionContext)
  }

  override def consume(id: String): Unit = {
    MailTokenUser.delete(id)
  }

  override def retrieve(id: String): Future[Option[MailTokenUser]] = {
    MailTokenUser.findById(id)
  }
}