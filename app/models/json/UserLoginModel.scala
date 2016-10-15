package models.views

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by Bulat on 29.09.2016.
  */
case class UserLoginModel(email: String, pswd: String)

object UserLoginModelImplicits {
  implicit val writes: Writes[UserLoginModel] = (
    (JsPath \ "email").write[String] and
      (JsPath \ "pswd").write[String]
    )(unlift(UserLoginModel.unapply))

  implicit val reads: Reads[UserLoginModel] = (
    (JsPath \ "email").read[String] and
      (JsPath \ "pswd").read[String]
    )(UserLoginModel.apply _)
}