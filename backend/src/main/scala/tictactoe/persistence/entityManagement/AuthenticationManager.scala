package tictactoe.persistence.entityManagement

import grizzled.slf4j.Logging
import org.bson.Document
import tictactoe.exceptions.PersistenceException._
import tictactoe.model.entity.UserId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor._
import tictactoe.persistence.entityManagement.AuthenticationManager.{PWHash, _}
import tictactoe.persistence.entityManagement.cache.EntityCache

import scala.collection.mutable
import scala.util.Random

/**
  */
class AuthenticationManager(connection: PersistenceAccessor[UserId]) extends Logging {

  private val tokenCache = new EntityCache[UserId, Authentication](ignore => {})

  private val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])([a-zA-Z0-9]{8,50})$"

  @throws[IllegalPasswordException]
  @throws[PasswordAlreadySetException]
  @throws[EntityNotFoundException]
  final def addPassword(id: UserId, password: String): Authentication =
    readPasswordHash(id) match {
      case Some(_) => throw PasswordAlreadySetException()
      case None =>
        checkAndWritePassword(id, password)
        tokenCache.add(Authentication(id, generateToken())).get
    }

  @throws[EntityNotFoundException]
  final def addPasswordHash(id: UserId, passwordHash: String): Unit = {
    connection.findAndUpdateDocument(id, new Document(pwHashKey, passwordHash))
  }


  @throws[IllegalPasswordException]
  @throws[PasswordNotSetException]
  final def resetPassword(id: UserId, password: String) =
    readPasswordHash(id) match {
      case None => throw PasswordNotSetException()
      case Some(_) =>
        checkAndWritePassword(id, password)
    }

  /** Check if the password comply with the requirement.
    *
    * @param password password
    * @throws IllegalPasswordException illegal password
    */
  @throws[IllegalPasswordException]
  final def checkPasswordSpecification(password: String): Unit = {
    if (!password.matches(PASSWORD_REGEX)) {
      throw IllegalPasswordException()
    }
  }

  @throws[EntityNotFoundException]
  private[entityManagement] def writePassword(id: UserId, password: String): Unit =
    connection.findAndUpdateDocument(id, new Document(pwHashKey, password.hashCode))

  @throws[EntityNotFoundException]
  @throws[IllegalPasswordException]
  private def checkAndWritePassword(id: UserId, password: String): Unit = {
    checkPasswordSpecification(password)
    writePassword(id, password)
  }


  @throws[EntityNotFoundException]
  @throws[WrongPasswordException]
  @throws[PasswordNotSetException]
  @throws[IllegalPasswordException]
  final def modifyPassword(id: UserId, password: String): Unit = resetPassword(id, password)

  @throws[EntityNotFoundException]
  final def readPasswordHash(id: UserId): Option[String] =
    connection.findFirstDocumentForID(id) match {
      case None => throw EntityNotFoundException()
      case Some(doc) => Option[String](doc.getString(pwHashKey))
    }


  @throws[EntityNotFoundException]
  @throws[WrongPasswordException]
  final def authenticatePlayer(email: String, password: String): Authentication =
    readIdAndPasswordHash(email) match {
      case (id, Some(hash)) if hash == password.hashCode => //will fall through if hash not equal
        getFromCache(id)
      case _ => throw WrongPasswordException()
    }


  @throws[EntityNotFoundException]
  private[entityManagement] def readIdAndPasswordHash(email: String): (UserId, Option[PWHash]) =
    connection.findFirstDocumentForEmail(email) match {
      case None => throw EntityNotFoundException()
      case Some(doc) => (UserId.ofStringID(doc.getString(idKey)), Option[Integer](doc.getInteger(pwHashKey)).asScala)
    }


  @throws[InvalidAuthenticationException]
  final def verifyAuthentication(auth: Authentication): Unit =
    tokenCache.get(auth.id) match {
      case Some(mutator) if mutator.get == auth => trace(s"auth: $auth is valid")
      case _ => throw InvalidAuthenticationException()
    }

  @throws[EntityNotFoundException]
  final def getPlayerIdByEmail(email: String): UserId =
    connection.findFirstDocumentForEmail(email) match {
      case None => throw EntityNotFoundException()
      case Some(doc) => UserId.ofStringID(doc.getString(idKey))
    }


  final def invalidateToken(id: UserId) = {
    getFromCache(id)
  }

  private def getFromCache(id: UserId): Authentication = {
    val mutator = tokenCache.getOrElseAdd(id, () => Authentication(id, generateToken()))
    mutator.update(_.copy(token = generateToken()))
    mutator.get
  }

}

object AuthenticationManager {
  type PWHash = Int
  private val TOKEN_LENGTH = 32

  /** Generate a token.
    *
    * @return token
    */
  private[AuthenticationManager] def generateToken(): String = {
    val builder = new mutable.StringBuilder(TOKEN_LENGTH)
    for (i <- 0 to TOKEN_LENGTH) {
      builder.append(Random.nextPrintableChar)
    }
    builder.toString
  }
}