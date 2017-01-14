package tictactoe.persistence.databaseAccess.cache

import org.bson.Document
import tictactoe.exceptions.PersistenceException
import PersistenceException.DuplicateKeyException
import tictactoe.model.entity.EntityId
import tictactoe.persistence.ReadWriteLockWrapper
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor._

import scala.collection.mutable

/**
  */
private[cache] class CacheAccessor[ID <: EntityId](
                                                    rwl: ReadWriteLockWrapper,
                                                    map: mutable.Map[String, Document]
                                                  ) extends PersistenceAccessor[ID] {
  def findAllDocumentsStream(): Stream[Document] = {
    rwl.readLock.lock()
    try
      map.valuesIterator.toStream
    finally
      rwl.readLock.unlock()
  }

  override def findAllDocuments(): List[Document] = findAllDocumentsStream().toList

  @throws[DuplicateKeyException]("If ID is already taken")
  private def checkUniqueFields(document: Document): Unit = {
    val email = document.get(emailKey)
    val id = document.get(idKey)
    if (email == null)
      return

    if (findAllDocumentsStream().exists(d => d.get(idKey) != id && d.get(emailKey) == email))
      throw DuplicateKeyException()

  }

  @throws[DuplicateKeyException]("If ID or E-Mail is already taken")
  override def writeDocument(document: Document): Unit = {
    val id = document.getString(idKey)
    rwl.writeLock.lock()
    try {
      checkUniqueFields(document)
      map.get(id) match {
        case None => map.put(id, document)
        case Some(_) => throw DuplicateKeyException()
      }
    } finally
      rwl.writeLock.unlock()
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  override protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean = {
    rwl.writeLock.lock()
    try {
      val existing = map.get(id.asString)
      if (existing.isEmpty)
        return false
      checkUniqueFields(document)

      val oldDoc = existing.get
      document.asStream.foreach(e => oldDoc.put(e.getKey, e.getValue))

      true
    } finally
      rwl.writeLock.unlock()
  }

  override def findFirstDocumentForID(id: ID): Option[Document] = {
    rwl.readLock.lock()
    try
      map.get(id.asString)
    finally
      rwl.readLock.unlock()
  }

  override def findFirstDocumentForEmail(email: String): Option[Document] = {
    rwl.readLock.lock()
    try {
      findAllDocumentsStream().find(_.getString(emailKey) == email)
    } finally
      rwl.readLock.unlock()
  }

}
