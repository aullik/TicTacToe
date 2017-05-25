package tictactoe.persistance

import org.bson.Document
import org.scalatest.{FlatSpec, Matchers}
import tictactoe.exceptions.PersistenceException.DuplicateKeyException
import tictactoe.persistence.Persistence
import scaldi.{Injectable, Injector, Module}
import tictactoe.persistence.databaseAccess.cache.CachePersistence
import tictactoe.persistence.databaseAccess.db4o.DB4OPersistence
import tictactoe.persistence.databaseAccess.file.FilePersistence
import tictactoe.persistence.databaseAccess.mongo.{MongoAccessor, MongoPersistence}
import tictactoe.persistence.databaseAccess.slick.SlickPersistence
/**
  * Created by Y on 08.04.2017.
  */
class PersistanceTest extends FlatSpec with Matchers with Injectable {

  def createUser(username: String, startId: String): Document= {
    val doc = new Document()
    doc.append("_id", startId+"58e2a2f73a465e4308f3d5b8")
    doc.append("name", username)
    doc.append("email", username+"@mail.de")
    doc.append("emailConfirmed", false)
    doc.append("key", "franz@mail.de")
    doc.append("passwordHash", "$2a$10$Fmqgm/D4BuhihJlL1ZmI1e41KIAMHBIIZ1K.3h7yKRuF4bDcpgiDa")
    doc
  }/*
  def getDocWithID(id:String): Document ={
    val doc = new Document()
    doc.append("_id", id)
    doc
  }*/
  private val user1 = createUser("franz", "1");
  private val user2 = createUser("manuel", "2");
  private val user3 = createUser("luise", "3");

  def persistenceBehavior(persistence: Persistence): Unit = {

   // val persistence = inject[Persistence]

    it should "save a user" in {
      persistence.userConnection.writeDocument(user1)
      persistence.userConnection.findAllDocuments() shouldBe List(user1)
    }

    it should "save a second user" in {
      persistence.userConnection.writeDocument(user2)
      val users = persistence.userConnection.findAllDocuments()
      users.size shouldBe 2
      users should contain(user1)
      users should contain(user2)
    }

    it should "save a third user" in {
      persistence.userConnection.writeDocument(user3)
      val users = persistence.userConnection.findAllDocuments()
      users.size shouldBe 3
      users should contain(user1)
      users should contain(user2)
      users should contain(user3)
    }

    it should "throw any exception, when saving an already existing user" in {
      intercept[DuplicateKeyException] {
        persistence.userConnection.writeDocument(user3)
      }
    }

    it should "find the first, second and third user in" in {
      persistence.userConnection.findFirstDocumentForEmail(user1.get("email").toString) shouldBe Option{user1}
      persistence.userConnection.findFirstDocumentForEmail(user2.get("email").toString) shouldBe Option{user2}
      persistence.userConnection.findFirstDocumentForEmail(user3.get("email").toString) shouldBe Option{user3}
    }



  }

  "mongoPersistence" should behave like persistenceBehavior(
    new MongoPersistence("localhost", 27017, "tictactoe-test")
  )
  "filePersistence" should behave like persistenceBehavior(
    new FilePersistence("persistence-test")
  )
  "cachePersistence" should behave like persistenceBehavior(
    new CachePersistence
  )

  "db4oPersistence" should behave like persistenceBehavior(
    new DB4OPersistence
  )
  "slickPersistence" should behave like persistenceBehavior(
    new SlickPersistence
  )

}/*
  extends FlatSpec with Matchers with Injectable {

  def persistenceBehavior(persistence: Persistence): Unit = {

    it should "first load empty list" in {
      val data = persistence.userConnection.findAllDocuments()
      println(data)
    }

  }


  "DB4OPersistence" should behave like persistenceBehavior({
    implicit object Injector extends Module {
      bind[Persistence] toProvider new DB4OPersistence
      bind[String] identifiedBy 'db4oDatabaseName to "testDb4oDatabase"
    }
    inject[Persistence]
  })
}
*/