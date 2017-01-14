package tictactoe.persistence.entityManagement

import grizzled.slf4j.Logging
import tictactoe.model.PersistenceException._
import tictactoe.model.entity.{Entity, EntityId}
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.parser.DocumentParser
import tictactoe.persistence.entityManagement.cache.EntityCache
import tictactoe.persistence.entityManagement.mutator.{CacheMutator, Mutator, ProxyMutator}


/**
  */
class EntityManager[ID <: EntityId, E <: Entity[ID]](connection: PersistenceAccessor[ID])
                                                    (implicit protected val entityManifest: Manifest[E]
                                                    ) extends Logging {

  private val cache = new EntityCache[ID, E](writeUpdatedEntity)

  @throws[DuplicateKeyException]
  final def add(entity: E): Mutator[ID, E] = {
    writeNewEntity(entity)
    proxy(entity.id, cache.add(entity))
  }

  @throws[DuplicateKeyException]
  private[entityManagement] def writeNewEntity(entity: E): Unit = {
    val document = DocumentParser.write(entity)
    connection.writeDocument(document)
  }

  @throws[EntityNotFoundException]
  @throws[DuplicateKeyException]
  private[entityManagement] def writeUpdatedEntity(entity: E): Unit = {
    val document = DocumentParser.write(entity)
    connection.findAndUpdateDocument(entity.id, document)
  }

  @throws[EntityNotFoundException]
  final def get(id: ID): Mutator[ID, E] = {
    proxy(id, getFromCache(id))
  }

  @throws[EntityNotFoundException]
  private[entityManagement] def readEntity(id: ID): E =
    connection.findFirstDocumentForID(id) match {
      case None => throw EntityNotFoundException()
      case Some(doc) => DocumentParser.read[E](doc)
    }

  def directUpdate(id: ID, entityUpdater: E => E): Unit = {
    get(id).update(entityUpdater)
  }

  private def getFromCache(id: ID): CacheMutator[ID, E] = {
    cache.getOrElseAdd(id, () => readEntity(id))
  }

  private def proxy(id: ID, mutator: CacheMutator[ID, E]): ProxyMutator[ID, E] = {
    new ProxyMutator[ID, E](() => getFromCache(id), cache.cacheDuration, mutator)
  }


}
