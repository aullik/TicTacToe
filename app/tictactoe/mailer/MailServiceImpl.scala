package tictactoe.mailer

import com.google.inject.Inject
import play.api.Configuration
import play.api.libs.mailer.{Email, MailerClient}
import grizzled.slf4j.Logging

/** A MailService used to send emails to players
  *
  */
trait MailService {
  def sendEmail(recipients: String*)(subject: String, bodyHtml: String, bodyText: String): Unit
}

/** MailerService Implementation
  *
  * @param mailerClient mailerClient which is used to send emails
  * @param conf         Play Configuration which contains from address
  */
class MailServiceImpl @Inject()(mailerClient: MailerClient, conf: Configuration) extends MailService with Logging {

  lazy val from = conf.underlying.getString("play.mailer.from")

  def sendEmail(recipients: String*)(subject: String, bodyHtml: String, bodyText: String) = {
    info(s"sendEmail(recipients=$recipients)")
    mailerClient.send(Email(subject, from, recipients, Some(bodyText), Some(bodyHtml)))
  }
}