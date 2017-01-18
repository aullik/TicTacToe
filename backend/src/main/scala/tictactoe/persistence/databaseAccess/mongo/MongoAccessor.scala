package tictactoe.persistence.databaseAccess.mongo

import com.mongodb.MongoException
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.{Filters, Projections}
import org.bson.Document
import tictactoe.model.entity.EntityId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor._
import MongoAccessor._
import tictactoe.exceptions.PersistenceException._

/**
  */
private[mongo] class MongoAccessor[ID <: EntityId](
                                                    collection: MongoCollection[Document]
                                                  ) extends PersistenceAccessor[ID] {


  override def findAllDocuments(): List[Document] = {
    collection.find().projection(Projections.include(idKey)).asList
  }


  override def findFirstDocumentForID(id: ID): Option[Document] = {
    collection.find(Filters.eq(idKey, id.asString)).asStream.headOption
  }

  override def findFirstDocumentForEmail(email: String): Option[Document] = {
    collection.find(Filters.eq(emailKey, email)).asStream.headOption
  }


  @throws[DuplicateKeyException]("If ID or E-Mail is already taken")
  override def writeDocument(document: Document) = {
    try {
      collection.insertOne(document)
    } catch {
      case e: MongoException if e.getCode == DUPLICATE_KEY_ERROR => throw DuplicateKeyException()
    }
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  override protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean = {
    try {
      val doc = collection.findOneAndUpdate(Filters.eq(idKey, id.asString),
        new Document("$set", document))
      doc != null
    } catch {
      case e: MongoException if e.getCode == DUPLICATE_KEY_ERROR => throw DuplicateKeyException()
    }
  }


}

object MongoAccessor {
  private val DUPLICATE_KEY_ERROR = 11000
}



