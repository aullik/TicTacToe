package tictactoe.controllers.webControllers


import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import grizzled.slf4j.Logging
import tictactoe.TicTacToeServer
import tictactoe.exceptions.PersistenceException._
import tictactoe.model.User
import tictactoe.model.entity.UserId
import tictactoe.persistence.entityManagement.mutator.Wrapper

import scala.util.{Failure, Random, Success, Try}

/**
  */
@Singleton
class UserController @Inject private(server: TicTacToeServer) extends Logging {
  private lazy val rnd: Random = new Random()

  //==============================================================================================

  /** Register and login a player.
    *
    * @param email e-mail
    * @param name  name
    * @return tictactoe.authentication of the player */
  @throws[IllegalNameException]
  @throws[IllegalEmailException]
  @throws[EmailInUseException]
  @throws[IllegalPasswordException]
  @throws[PasswordsDoNotMatchException]
  def register(email: String,
               name: String): User = {
    info(s"register(email=$email, name=$name, password=***, passwordRepetition=***)")
    val usr = User(email = email, name = name)
    server.persistence.userManager.add(usr)
    usr
  }


  def confirmEmail(email: String): User = {
    info(s"confirmEmail(email=$email")
    val targetId = server.persistence.authenticationManager.getPlayerIdByEmail(email)
    server.persistence.userManager.directUpdate(targetId, _.copy(emailConfirmed = true))
    get(targetId).get
  }

  def get(id: UserId): Wrapper[User] = {
    info(s"getPlayer(id=$id)")
    server.persistence.userManager.get(id).wrapper
  }


  /** Get a player by email
    *
    * @param email email of the user
    * @return Player
    */
  @throws[EntityNotFoundException]("Player not found")
  def getUserByEmail(email: String): Wrapper[User] = {
    val playerId = server.persistence.authenticationManager.getPlayerIdByEmail(email)
    get(playerId)
  }


  def modifyPasswordHash(userId: UserId,
                         passwordHash: String): Unit = {
    info(s"modifyPasswordHash(auth.id=$userId, passwordHash=***)")
    server.persistence.authenticationManager.addPasswordHash(userId, passwordHash)
  }

  def getPassword(userId: UserId): String = {
    info(s"getPassword(playerId=$userId")
    server.persistence.authenticationManager.readPasswordHash(userId).get
  }

  def saveOauth(profile: CommonSocialProfile): User = {
    Try {
      getUserByEmail(profile.email.get)
    } match {
      case Success(user) => user.get
      case Failure(_) =>
        server.persistence.userManager.add(
          buildUserOfCommonSocialProfile(profile)
        ).get
    }
  }

  private def nextChar(): Char = {
    //97-122 -> a-z
    (rnd.nextInt(122 - 97 + 1) + 97).toChar
  }

  private def buildUserOfCommonSocialProfile(profile: CommonSocialProfile): User = {
    val email =
      profile.email match {
        case Some(em) => em
        case None =>
          warn("no email set")
          val sb = new StringBuilder()
          for (i <- 1 to 6)
            sb.append(nextChar())
          sb.append('@')
          for (i <- 1 to 4)
            sb.append(nextChar())
          sb.append(".de")
          sb.toString()
      }
    val usrName =
      profile.fullName match {
        case Some(un) => un
        case None =>
          warn("no username set")
          email.split('@')(0)
      }
    User(UserId(), usrName, email)
  }

}


