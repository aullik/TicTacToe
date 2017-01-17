package tictactoe.model

import tictactoe.model.entity.{Entity, UserId}
import tictactoe.silhouette.IdentitySilhouette

/**
  */
case class User(id: UserId = UserId(),
                name: String,
                token: String = "",
                email: String,
                emailConfirmed: Boolean = false
               ) extends Entity[UserId] with IdentitySilhouette {

  val key = email
}
