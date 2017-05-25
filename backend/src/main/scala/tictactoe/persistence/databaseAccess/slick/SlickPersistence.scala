package tictactoe.persistence.databaseAccess.slick


import tictactoe.model.entity.EntityId
import tictactoe.persistence.Persistence
import tictactoe.persistence.databaseAccess.PersistenceAccessor

/** Persistence-Implementation for MongoDB. */
class SlickPersistence() extends Persistence {

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] =
    new SlickAccessor("slick-"+name)
}