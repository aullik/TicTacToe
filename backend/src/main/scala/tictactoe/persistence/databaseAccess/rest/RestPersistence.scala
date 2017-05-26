package tictactoe.persistence.databaseAccess.rest

import com.db4o.Db4oEmbedded
import tictactoe.model.entity.EntityId
import tictactoe.persistence.Persistence
import tictactoe.persistence.databaseAccess.PersistenceAccessor

/** Persistence-Implementation for MongoDB. */
class RestPersistence(url: String) extends Persistence {

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] = {
    new RestAccessor(url+"/"+name)
  }
}