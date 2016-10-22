package models.json

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by Bulat on 13.09.2016.
  */
case class DFDiagram(id: UUID, nodes: Seq[DFNode], links: Map[UUID, UUID])

case class DFNode(id: UUID, category: String, data: String)

object DataFlowDiagram {

  implicit val writesLinks: Writes[Map[UUID, UUID]] = new Writes[Map[UUID, UUID]] {
    override def writes(o: Map[UUID, UUID]): JsValue = {
      val newMap = o map { kv => kv._1.toString -> kv._2.toString }
      Json.toJson(newMap)
    }
  }

  implicit val readsLinks: Reads[Map[UUID, UUID]] = new Reads[Map[UUID, UUID]] {
    override def reads(json: JsValue): JsResult[Map[UUID, UUID]] =
      JsSuccess(json.as[Map[String, String]] map {
        case (k, v) => java.util.UUID.fromString(k) -> java.util.UUID.fromString(v)
      })
  }

  implicit val writesNode: Writes[DFNode] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "category").write[String] and
      (JsPath \ "data").write[String]
    ) (unlift(DFNode.unapply))

  implicit val readsNode: Reads[DFNode] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "category").read[String] and
      (JsPath \ "data").read[String]
    ) (DFNode.apply _)

  implicit val writesDiagram: Writes[DFDiagram] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "nodes").write[Seq[DFNode]] and
      (JsPath \ "links").write[Map[UUID, UUID]]
    ) (unlift(DFDiagram.unapply))

  implicit val readsDiagram: Reads[DFDiagram] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "nodes").read[Seq[DFNode]] and
      (JsPath \ "links").read[Map[UUID, UUID]]
    ) (DFDiagram.apply _)
}