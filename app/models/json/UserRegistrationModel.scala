package models.views

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}

/**
  * Created by Bulat on 13.09.2016.
  */
case class UserRegistrationModel(fullname: String, email: String, pswd: String)

object UserRegistrationModelImplicits {
  implicit val writes: Writes[UserRegistrationModel] = (
    (JsPath \ "fullname").write[String] and
      (JsPath \ "email").write[String] and
      (JsPath \ "pswd").write[String]
    )(unlift(UserRegistrationModel.unapply))

  implicit val reads: Reads[UserRegistrationModel] = (
    (JsPath \ "fullname").read[String] and
      (JsPath \ "email").read[String] and
      (JsPath \ "pswd").read[String]
    )(UserRegistrationModel.apply _)
}