package tictactoe.persistence.databaseAccess.parser

import java.util.Locale

import org.bson.Document
import org.json4s.FieldSerializer._
import org.json4s.JsonAST._
import org.json4s.jackson.Serialization
import org.json4s.{CustomSerializer, Extraction, FieldSerializer, Formats, NoTypeHints, Serializer}
import tictactoe.model.entity.{EntityId, GameId, IDFactory, UserId}


/**
  * TODO
  */
object DocumentParser {

  def read[T](document: Document)(implicit manifest: Manifest[T]): T = {
    Serialization.read[T](document.toJson)(formats, manifest)
  }

  def write[T <: AnyRef](t: T): Document = {
    Document.parse(Serialization.write(t)(formats))
  }

  private def extract[T](list: List[(String, JValue)])(implicit manifest: Manifest[T]): T = {
    JObject(list).extract[T](formats, manifest)
  }

  private def decompose[T](t: T): JValue = {
    Extraction.decompose(t)(formats)
  }


  /**
    * Serializer for Locale.
    */
  private object LocaleSerializer extends CustomSerializer[Locale](formats =>
    ( {
      case JString(s) => s match {
        case "en" => Locale.ENGLISH
        case "de" => Locale.GERMAN
      }
    }, {
      case locale: Locale => JString(locale.toString)
    })
  )

  private object PairSerializer extends CustomSerializer[(Int, Int)](formats =>
    ( {
      case JObject(list) =>
        val _1: Int = list.find(field => field._1 == "_1").get._2.values.asInstanceOf[BigInt].toInt
        val _2: Int = list.find(field => field._1 == "_2").get._2.values.asInstanceOf[BigInt].toInt
        (_1, _2)
    }, {
      case pair: (Any, Any) =>
        JObject(List(
          JField("_1", JInt(BigInt(pair._1.asInstanceOf[Int]))),
          JField("_2", JInt(BigInt(pair._2.asInstanceOf[Int])))
        ))
    })
  )


  private object EntityIdSerializer {
    val EntityIdDeserializer: PartialFunction[Any, org.json4s.JValue] = {
      case id: EntityId => JString(id.asString)

    }
    val entityIdSerializer = List(
      new EntityIdSerializer[GameId](GameId),
      new EntityIdSerializer[UserId](UserId))
  }

  private class EntityIdSerializer[ID <: EntityId](factory: IDFactory[ID])(implicit mf: Manifest[ID]) extends CustomSerializer[ID](formats => {
    ( {
      case JString(s) => factory.ofStringID(s)
    }, EntityIdSerializer.EntityIdDeserializer)
  })

  private object IdSerializer extends FieldSerializer[Object](
    renameTo("id", "_id"),
    renameFrom("_id", "id")
  )

  /** Json4s formats. */
  val baseFormats: Formats =
    Serialization.formats(NoTypeHints) + IdSerializer

  val serializer: List[Serializer[_]] = List(
    LocaleSerializer,
    PairSerializer
  ) ++ EntityIdSerializer.entityIdSerializer

  val formats: Formats = baseFormats ++ serializer

}
