package tictactoe.persistence.databaseAccess.rest

import java.io.File

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.bson.Document
import tictactoe.exceptions.PersistenceException.DuplicateKeyException
import tictactoe.model.entity.EntityId
import org.json4s.jackson.Serialization.{read, write}
import play.api.libs.json._
import play.api.libs.functional.syntax._

import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.databaseAccess.PersistenceAccessor.pwHashKey
import tictactoe.persistence.databaseAccess.parser.DocumentParser

import scala.language.postfixOps
import play.api.libs.ws
import play.api.libs.ws.ahc.AhcWSClient
import scaldi.{Injectable, Injector, Module}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps
/**
  * Created by Y on 03.04.2017.
  */
private[rest] class RestAccessor[ID <: EntityId](url: String) extends PersistenceAccessor[ID] {
  val conf = new File("web/conf/application.conf")
  implicit val system = ActorSystem("systemTwo", ConfigFactory.parseFile(conf).resolve())
  implicit val materializer = ActorMaterializer()
  val ws = AhcWSClient()

  override def findAllDocuments(): List[Document] = {
    println("findAllDocuments")
    val request = ws.url(s"$url").get()
    val result = Await.result(request, 5 seconds)
    if(result.status != 200) {
      throw new IllegalStateException("Status:" + result.status)
    }
    println(result.json.toString())
    null
  }

  override def findFirstDocumentForID(id: ID): Option[Document] = {
    println("findFirstDocumentForID")
    val request = ws.url(s"$url/"+id).get()
    val result = Await.result(request, 5 seconds)
    if(result.status != 200) {
      throw new IllegalStateException("Status:" + result.status)
    }
    println(result.json.toString())
    None
  }
  def toDocument(data: User): Document = {
    val doc = new Document()
    doc.append("_id", data.id)
    doc.append("name", data.name)
    doc.append("email", data.email)
    doc.append("emailConfirmed", data.emailConfirmed)
    doc.append("key", data.key)
    doc.append("passwordHash", data.passwordHash)
    doc
  }

  case class User(id: String, name: String, email: String, emailConfirmed: Boolean, key: String, passwordHash: String)
  implicit val picReads: Reads[User] = (
    (JsPath \ "_id").read[String] and
      (JsPath \ "name").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "emailConfirmed").read[Boolean] and
      (JsPath \ "key").read[String] and
      (JsPath \ "passwordHash").read[String]
    )(User.apply _)

  override def findFirstDocumentForEmail(email: String): Option[Document] = {
    println("findFirstDocumentForEmail")
    val request = ws.url(s"$url/email/"+email).get()
    val result = Await.result(request, 5 seconds)
    if(result.status != 200) {
      throw new IllegalStateException("Status:" + result.status)
    }
    println("resultat "+result.json.toString())
    result.json.validate[User] match {
      case s: JsSuccess[User] => Option{toDocument(s.get)};
      case e: JsError => None
    }
  }

  override def writeDocument(document: Document): Unit = {
    println("writeDocument")
    println(document.toJson)
    val request = ws.url(s"$url").post(document.append("passwordHash","").toJson)
    val result = Await.result(request, 5 seconds)
    println(result.body)
    if(result.status != 200) {
      throw new IllegalStateException("Status:" + result.status)
    }
    println("resultatwrite "+result.json.toString())
    None
    throw DuplicateKeyException();
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  override protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean = ???
}
