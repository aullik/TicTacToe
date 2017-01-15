package tictactoe.persistence.entityManagement

import tictactoe.model.entity.{Entity, UserId}

/** Object to authenticate a player.
  *
  * @param id    id of the User
  * @param token token of the User
  */
case class Authentication(id: UserId, token: String) extends Entity[UserId]
