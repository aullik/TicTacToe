package silhouette

import org.joda.time.DateTime

/** Token for emails
  *
  */
trait MailToken {
  def id: String

  def email: String

  def expirationTime: DateTime

  def isExpired = expirationTime.isBeforeNow
}
