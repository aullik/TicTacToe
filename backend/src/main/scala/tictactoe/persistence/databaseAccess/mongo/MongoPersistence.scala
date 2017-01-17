package tictactoe.persistence.databaseAccess.mongo

import com.mongodb.MongoClient
import tictactoe.model.entity.EntityId
import tictactoe.persistence.Persistence
import tictactoe.persistence.databaseAccess.PersistenceAccessor

/** Persistence-Implementation for MongoDB. */
class MongoPersistence(host: String, port: Int, databaseName: String) extends Persistence {

  private val db = new MongoClient(host, port).getDatabase(databaseName)

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] =
    new MongoAccessor(db.getCollection(name))
}
