package tictactoe.persistence.databaseAccess

import java.{util => ju}

import com.mongodb.client.FindIterable
import org.bson.Document
import tictactoe.model.PersistenceException.{DuplicateKeyException, EntityNotFoundException}
import tictactoe.model.entity.EntityId

import scala.collection.Iterator
import scala.collection.convert.WrapAsScala._
import scala.language.implicitConversions

/**
  */
trait PersistenceAccessor[ID <: EntityId] {


  def findAllDocuments(): List[Document]

  def findFirstDocumentForID(id: ID): Option[Document]

  def findFirstDocumentForEmail(email: String): Option[Document]

  @throws[DuplicateKeyException]("If ID or E-Mail is already taken")
  def writeDocument(document: Document)


  /**
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    */
  @throws[EntityNotFoundException]
  @throws[DuplicateKeyException]
  def findAndUpdateDocument(id: ID, document: Document): Unit = {
    if (!findAndUpdateDocumentIfPresent(id, document))
      throw EntityNotFoundException()
  }

  /**
    *
    * @param id       the id of the player
    * @param document the document containing the key-value sets to be replaced
    * @return true if a document has been updated, false if no document with the given id was present
    */
  @throws[DuplicateKeyException]("if email is already taken")
  protected def findAndUpdateDocumentIfPresent(id: ID, document: Document): Boolean
}

object PersistenceAccessor {
  val pwHashKey = "passwordHash"
  val emailKey = "email"
  val idKey = "_id"


  class IterableConverter[TR](iter: Iterator[TR]) {
    def asIterator: Iterator[TR] = iter

    def asStream: Stream[TR] = iter.toStream

    def asList: List[TR] = iter.toList
  }

  class OptionConverter[JavaType, ScalaType](opt: Option[JavaType], convert: JavaType => ScalaType) {
    def asScala: Option[ScalaType] = opt.map(convert(_))

  }

  implicit def findIterableToConverter[TR](fi: FindIterable[TR]): IterableConverter[TR] =
    new IterableConverter(asScalaIterator(fi.iterator()))

  implicit def documentToConverter(doc: Document): IterableConverter[ju.Map.Entry[String, AnyRef]] =
    new IterableConverter(asScalaIterator(doc.entrySet().iterator()))

  implicit def optionToConverter(opt: Option[Integer]): OptionConverter[Integer, Int] =
    new OptionConverter[Integer, Int](opt, Integer2int)
}
