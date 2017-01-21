package tictactoe.mailer

import java.util.UUID

import org.joda.time.DateTime
import tictactoe.silhouette.MailToken

import scala.concurrent.Future

/** A Token which is used to reset a password or confirm a user's email during a sign up.
  *
  * @param id             id of token
  * @param email          email of token user
  * @param expirationTime expiration time of token
  * @param isSignUp       is sign up token
  */
case class MailTokenUser(id: String, email: String, expirationTime: DateTime, isSignUp: Boolean) extends MailToken

/** Factory for [[MailTokenUser]] instances.
  * TODO: Refactor to store tokens persistence?
  */
object MailTokenUser {

  /** Creates a MailTokenUser with given email and isSignUp
    *
    * @param email    email address of token user
    * @param isSignUp used for signUp
    * @return a new MailTokenUser
    */
  def apply(email: String, isSignUp: Boolean): MailTokenUser =
    MailTokenUser(UUID.randomUUID().toString, email, new DateTime().plusHours(24), isSignUp)

  val tokens = scala.collection.mutable.HashMap[String, MailTokenUser]()

  /** Find token by id
    *
    * @param id of token
    * @return token with given id
    */
  def findById(id: String): Future[Option[MailTokenUser]] = {
    Future.successful(tokens.get(id))
  }

  /** Saves a token in HashMap
    *
    * @param token token to save
    * @return saved token
    */
  def save(token: MailTokenUser): Future[MailTokenUser] = {
    tokens += (token.id -> token)
    Future.successful(token)
  }

  /** Deletes token from tokens
    *
    * @param id token of id
    */
  def delete(id: String): Unit = {
    tokens.remove(id)
  }
}
