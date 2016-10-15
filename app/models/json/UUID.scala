package models.json

import play.api.data.validation.ValidationError
import play.api.libs.json._

import scala.util.Try

/**
  * Created by Bulat on 15.10.2016.
  * From: https://github.com/Coiney/play-json-extras/blob/master/src/main/scala/com/coiney/play/json/extras/UUID.scala
  */
object UUID {

  implicit val uuidFormat: Format[java.util.UUID] = Format(UUIDReads, UUIDWrites)

  object UUIDReads extends Reads[java.util.UUID] {
    def reads(json: JsValue) = json match {
      case JsString(s) =>
        parseUUID(s).map(JsSuccess(_)).getOrElse(JsError(Seq(JsPath() -> Seq(ValidationError("Expected UUID string")))))
      case _ =>
        JsError(Seq(JsPath() -> Seq(ValidationError("Expected UUID string"))))
    }

    def parseUUID(s: String): Option[java.util.UUID] = Try(java.util.UUID.fromString(s)).toOption
  }

  object UUIDWrites extends Writes[java.util.UUID] {
    def writes(uuid: java.util.UUID): JsValue = JsString(uuid.toString)
  }

}
