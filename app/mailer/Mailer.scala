package mailer

import play.api.i18n.Messages
import play.twirl.api.Html
import tictactoe.model.User
import views.html.mails

/** Mailer to communicate with Players
  *
  */
object Mailer {

  def html2String(html: Html): String = html.toString

  /** Sends a welcome message to a fresh registered Player so they can confirm there email address
    *
    * @param player the player that just registered
    * @param link   link to confirm email address
    * @param ms     MailService which is used to send the email to the player
    * @param m      Messages which is used for translation
    */
  def welcome(player: User, link: String)(implicit ms: MailService, m: Messages) {
    ms.sendEmail(player.email)(
      subject = Messages("mail.welcome.subject"),
      bodyHtml = html2String(mails.welcome(player.name, link)),
      bodyText = html2String(mails.welcomeTxt(player.name, link))
    )
  }

  def forgotPassword(email: String, link: String)(implicit ms: MailService, m: Messages) {
    ms.sendEmail(email)(
      subject = Messages("mail.forgotPassword.subject"),
      bodyHtml = html2String(mails.forgotPassword(email, link)),
      bodyText = html2String(mails.forgotPasswordTxt(email, link))
    )
  }
}
