package tictactoe.persistence

import tictactoe.model.User
import tictactoe.model.entity.{EntityId, GameId, UserId}
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.entityManagement.{AuthenticationManager, EntityManager, EntitySearcher}

trait Persistence {

  protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID]

  // Players
  lazy val userConnection: PersistenceAccessor[UserId] = createPersistenceConnection("users")
  lazy val authenticationManager = new AuthenticationManager(userConnection)
  lazy val userManager = new EntityManager[UserId, User](userConnection)
  lazy val userSearcher = new EntitySearcher[UserId](UserId, userConnection)

  // Games
  lazy val gameConnection: PersistenceAccessor[GameId] = createPersistenceConnection("games")
  //  lazy val gameManager = new EntityManager[GameId, Game](gameConnection)
  //  lazy val gameSearcher = new EntitySearcher[GameId](GameId, gameConnection)


}
