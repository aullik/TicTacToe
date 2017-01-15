package tictactoe.model

import tictactoe.model.entity.{Entity, UserId}

/**
  */
case class User(id: UserId, name: String, token: String, email: String) extends Entity[UserId] {
}
