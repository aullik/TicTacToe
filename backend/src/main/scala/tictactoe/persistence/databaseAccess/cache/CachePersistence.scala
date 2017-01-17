package tictactoe.persistence.databaseAccess.cache

import org.bson.Document
import tictactoe.model.entity.EntityId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.{Persistence, ReadWriteLockWrapper}

import scala.collection.mutable

class CachePersistence() extends Persistence {
  private val rwl = new ReadWriteLockWrapper()

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] = {
    val map = mutable.Map.empty[String, Document]
    new CacheAccessor(rwl, map)
  }
}
