package models.json

import java.util.UUID

import play.api.libs.functional.syntax._
import play.api.libs.json._

/**
  * Created by Bulat on 13.09.2016.
  */
case class DataFlowDiagram(id: UUID, nodes: Seq[DataFlowNode], links: Map[UUID, UUID])

case class DataFlowNode(id: UUID, category: String, data: String)

object DataFlowDiagramImplicits {
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

  implicit val writesNode: Writes[DataFlowNode] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "category").write[String] and
      (JsPath \ "data").write[String]
    ) (unlift(DataFlowNode.unapply))

  implicit val readsNode: Reads[DataFlowNode] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "category").read[String] and
      (JsPath \ "data").read[String]
    ) (DataFlowNode.apply _)

  implicit val writesDiagram: Writes[DataFlowDiagram] = (
    (JsPath \ "id").write[UUID] and
      (JsPath \ "nodes").write[Seq[DataFlowNode]] and
      (JsPath \ "links").write[Map[UUID, UUID]]
    ) (unlift(DataFlowDiagram.unapply))

  implicit val readsDiagram: Reads[DataFlowDiagram] = (
    (JsPath \ "id").read[UUID] and
      (JsPath \ "nodes").read[Seq[DataFlowNode]] and
      (JsPath \ "links").read[Map[UUID, UUID]]
    ) (DataFlowDiagram.apply _)
}