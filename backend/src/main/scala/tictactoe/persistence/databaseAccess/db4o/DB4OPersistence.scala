package tictactoe.persistence.databaseAccess.db4o

import com.db4o.Db4oEmbedded
import tictactoe.model.entity.EntityId
import tictactoe.persistence.Persistence
import tictactoe.persistence.databaseAccess.PersistenceAccessor

/** Persistence-Implementation for MongoDB. */
class DB4OPersistence() extends Persistence {

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] = {
    val db = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), "db4-"+name)
    new DB4OAccessor(db)
  }
}