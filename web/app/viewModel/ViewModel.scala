package viewModel

import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization
import play.api.libs.json._
import play.api.mvc.AnyContent

/**
  */
object ViewModel {

  implicit val formats = Serialization.formats(NoTypeHints)


  object Path {
    def apply[F](path: String)(implicit f: Format[F]): OFormat[F] = {
      (JsPath \ path).format[F]
    }
  }

  def read[T](requestBody: AnyContent)(implicit f: Format[T]): Option[T] = {
    requestBody.asJson.flatMap(validate[T](_))
  }

  private def validate[T](jsv: JsValue)(implicit f: Format[T]): Option[T] = {
    jsv.validate[T].asOpt
  }

}

