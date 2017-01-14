package tictactoe.model

import java.util.{Locale, ResourceBundle}

sealed abstract class PersistenceException(name: String) extends RuntimeException {

  final def getMessage(locale: Locale): String = {
    ResourceBundle.getBundle("ExceptionMessages", locale).getString(name)
  }

  override final def getMessage: String = {
    getMessage(Locale.getDefault)
  }

}

object PersistenceException {

  case class IllegalEmailException() extends PersistenceException("IllegalEmail")

  case class IllegalNameException() extends PersistenceException("IllegalName")

  case class IllegalPasswordException() extends PersistenceException("IllegalPassword")

  case class EmailInUseException() extends PersistenceException("EmailInUse")

  case class NameInUseException() extends PersistenceException("NameInUse")

  case class PasswordAlreadySetException() extends PersistenceException("PasswordAlreadySet")

  case class EntityNotFoundException() extends PersistenceException("EntityNotFound")

  case class DuplicateKeyException() extends PersistenceException("DuplicateKey")


}
