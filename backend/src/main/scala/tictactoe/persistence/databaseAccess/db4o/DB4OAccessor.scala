package tictactoe.persistence.databaseAccess.db4o


import com.db4o.query.Predicate
import tictactoe.exceptions.PersistenceException.DuplicateKeyException

import scala.language.postfixOps
import org.bson.Document
import tictactoe.model.User
import tictactoe.model.entity.EntityId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor.pwHashKey
import tictactoe.persistence.databaseAccess.parser.DocumentParser
import com.db4o.{Db4oEmbedded, EmbeddedObjectContainer}

import scala.collection.JavaConversions._
/**
  * Created by Y on 03.04.2017.
  */
private[db4o] class DB4OAccessor[ID <: EntityId](db: EmbeddedObjectContainer) extends PersistenceAccessor[ID] {

  case class Db4oEntry(_id: String,
                       name: String,
                       email: String,
                       emailConfirmed: Boolean = false,
                       key: String,
                       passwordHash: String)
/*
  private def doDatabaseAction[T](f: ObjectContainer => T): T = {
    val db = Db4oEmbedded.openFile(
      Db4oEmbedded.newConfiguration(),
      "db4o-database2.db"
    )
    try {
      f(db)
    } finally {
      db.close()
    }
  }*/

  def toDocument(head: Db4oEntry): Document = {
    val doc = new Document()
    doc.append("_id", head._id)
    doc.append("name", head.name)
    doc.append("email", head.email)
    doc.append("emailConfirmed", head.emailConfirmed)
    doc.append("key", head.key)
    doc.append("passwordHash", head.passwordHash)
    doc
  }


  override def findAllDocuments(): List[Document] =  {
    val queryList = db.query(classOf[Db4oEntry])
    queryList.to[List].map(x => toDocument(x))
  }

  override def findFirstDocumentForID(id: ID): Option[Document] =  {
    val query = db.query(new Predicate[Db4oEntry] {
      override def `match`(extend: Db4oEntry): Boolean =  {
        id.asString == extend._id
      }
    })
    if (query.size != 1) {
      None
    }else{
      Option{toDocument(query.get(0))}
    }
  }

  override def findFirstDocumentForEmail(email: String): Option[Document] = {
    val query = db.query(new Predicate[Db4oEntry] {
      override def `match`(extend: Db4oEntry): Boolean =  { email == extend.email }})
    if (query.size != 1) {
      None
    }else{
      Option{toDocument(query.get(0))}
    }
  }

  override def writeDocument(document: Document): Unit = {
    val query = db.query(new Predicate[Db4oEntry] {
      override def `match`(extend: Db4oEntry): Boolean =  {
        DocumentParser.read[User](document).id.toString == extend._id
      }
    })
    if (query.size > 0) {
      throw DuplicateKeyException();
    }else{
      var pass = "";
      if(document.get("passwordHash") != null ){
        pass = document.get("passwordHash").toString
      }
      val dbEntry = Db4oEntry(DocumentParser.read[User](document).id.asString,
        DocumentParser.read[User](document).name.toString,
        DocumentParser.read[User](document).email.toString,
        DocumentParser.read[User](document).emailConfirmed,
        DocumentParser.read[User](document).key.toString, pass)
      db.store(dbEntry)
    }
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  override protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean = {
    val query = db.query(new Predicate[Db4oEntry] { override def `match`(extend: Db4oEntry): Boolean =  { id.asString == extend._id }})
    if (query.size() > 0) {
      var entry: Db4oEntry = null;
      if(document.getString(pwHashKey) != null){
        entry = new Db4oEntry(query.get(0)._id, query.get(0).name, query.get(0).email, query.get(0).emailConfirmed, query.get(0).key, document.getString(pwHashKey))
      }else if(document.getBoolean("emailConfirmed")){
        entry = new Db4oEntry(query.get(0)._id, query.get(0).name, query.get(0).email, document.getBoolean("emailConfirmed"), query.get(0).key, query.get(0).passwordHash)

      }
      db.delete(query.get(0))
      db.store(entry)
      true
    }else{
      false
    }
  }
}
