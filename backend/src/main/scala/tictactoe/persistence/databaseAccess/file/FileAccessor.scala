package tictactoe.persistence.databaseAccess.file

import java.io.File

import org.bson.Document
import tictactoe.exceptions.PersistenceException
import PersistenceException.DuplicateKeyException
import tictactoe.model.entity.EntityId
import tictactoe.persistence.ReadWriteLockWrapper
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor._
import tictactoe.persistence.databaseAccess.file.FilePersistenceHelper._


/**
  */
private[file] class FileAccessor[ID <: EntityId]
(rwl: ReadWriteLockWrapper, dir: File) extends PersistenceAccessor[ID] {
  dir.mkdirs()

  private def fileToDocument(f: File): Document = {
    Document.parse(readFile(f))
  }

  private def findAllDocumentsStream(): Stream[Document] = {
    rwl.readLock.lock()
    try
      dir.listFiles().toStream.map(fileToDocument(_))
    finally
      rwl.readLock.unlock()
  }

  override def findAllDocuments(): List[Document] = {
    findAllDocumentsStream().toList
  }

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
    rwl.writeLock.lock()
    try {
      checkUniqueFields(document)

      val file = new File(dir.getPath + "/" + document.get(idKey) + ".json")
      if (!file.createNewFile())
        throw DuplicateKeyException()

      writeFile(file, document.toJson)
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
    val file = new File(dir.getPath + "/" + id.asString + ".json")
    rwl.writeLock.lock()
    try {
      if (!file.exists)
        return false

      checkUniqueFields(document)

      val oldDoc = fileToDocument(file)
      document.asStream.foreach(e => oldDoc.put(e.getKey, e.getValue))

      writeFile(file, oldDoc.toJson)
      true
    } finally
      rwl.writeLock.unlock()
  }

  override def findFirstDocumentForID(id: ID): Option[Document] = {
    val file = new File(s"${dir.getPath}/${id.asString}.json")
    rwl.readLock.lock()
    try {
      if (file.exists)
        Some(Document.parse(readFile(file)))
      else
        None
    } finally
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

