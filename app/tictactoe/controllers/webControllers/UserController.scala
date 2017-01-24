package tictactoe.controllers.webControllers


import com.google.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import grizzled.slf4j.Logging
import tictactoe.TicTacToeServer
import tictactoe.exceptions.PersistenceException._
import tictactoe.model.User
import tictactoe.model.entity.UserId
import tictactoe.persistence.entityManagement.mutator.Wrapper

import scala.util.{Failure, Success, Try}

/**
  */
@Singleton
class UserController @Inject private(server: TicTacToeServer) extends Logging {

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
        warn(profile.fullName)
        warn(profile.email)
        warn(profile.firstName)
        warn(profile.lastName)

        server.persistence.userManager.add(
          User(
            name = profile.fullName.get,
            email = profile.email.get
          )).get
    }
  }

}


