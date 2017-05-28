package tictactoe.persistence.databaseAccess.slick

import slick.lifted.TableQuery
import tictactoe.exceptions.PersistenceException.DuplicateKeyException
import slick.dbio.NoStream
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try
import org.bson.Document
import tictactoe.model.User
import tictactoe.model.entity.EntityId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor.{pwHashKey}
import tictactoe.persistence.databaseAccess.parser.DocumentParser

/**
  * Created by Y on 03.04.2017.
  */
private[slick] class SlickAccessor[ID <: EntityId](name: String) extends PersistenceAccessor[ID] {

  var table = TableQuery[Users]

  if(name.equals("users")){
    table = TableQuery[Users]
  }

  Try(doDatabaseAction(DBIO.seq(
    table.schema.create
  )))


  private def toDocument(head: (String, String, String, Boolean, String, String)): Document = {
    val doc = new Document()
    doc.append("_id", head._1)
    doc.append("name", head._2)
    doc.append("email", head._3)
    doc.append("emailConfirmed", head._4)
    doc.append("key", head._5)
    doc.append("passwordHash", head._6)
    doc
  }
  private def doDatabaseAction[R](query: DBIOAction[R, NoStream, Nothing]): R = {
    val db = Database.forURL("jdbc:h2:~/tictactoeThreeD-test2;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "sa")
    try {
      Await.result(db.run(query), 5 seconds)
    } finally {
      db.close()
    }
  }

  override def findAllDocuments(): List[Document] = {
    doDatabaseAction(table.result).to[List].map(x => toDocument(x))
  }

  override def findFirstDocumentForID(id: ID): Option[Document] = {
    val query = table.filter(tbl => tbl._id === id.asString)
    val data = doDatabaseAction(query.result)
    if(data.nonEmpty){
      Option{toDocument(data.head)}
    }else {
      None
    }
  }

  override def findFirstDocumentForEmail(email: String): Option[Document] = {
    val query = table.filter(tbl => tbl.email === email)
    val data = doDatabaseAction(query.result)
    if(data.nonEmpty){
      Option{toDocument(data.head)}
    }else {
      None
    }
  }

  @throws[DuplicateKeyException]("If ID or E-Mail is already taken")
  override def writeDocument(document: Document): Unit = {
    val query = table.filter(tbl => tbl._id === DocumentParser.read[User](document).id.toString)
    val data = doDatabaseAction(query.result)
    if(data.isEmpty) {
      var pass = "";
      if(document.get("passwordHash") != null ){
        pass = document.get("passwordHash").toString
      }
      doDatabaseAction(
        DBIO.seq(
          table += (DocumentParser.read[User](document).id.toString,
            DocumentParser.read[User](document).name.toString,
            DocumentParser.read[User](document).email.toString,
            (DocumentParser.read[User](document).emailConfirmed == true),
            DocumentParser.read[User](document).key.toString, pass)
        )
      )
    }else{
      throw DuplicateKeyException();
    }
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  override protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean = {
    val query =table.filter(_._id === id.asString)
    val data = doDatabaseAction(query.result)
    val action = query.map(user =>
      (user.passwordHash)
    ).update(
      (document.getString(pwHashKey))
    )
    doDatabaseAction(action)
    val query1 =table.filter(_._id === id.asString)
    val data1 = doDatabaseAction(query1.result)
    data1.head._6 == document.getString(pwHashKey)
  }
}
