package controllers

import java.util.UUID

import com.google.inject.Inject
import dao.{DataFlowDAO, SessionDAO}
import models.json.DFNode
import models.json.DFDiagram
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

/**
  * Created by Bulat on 13.09.2016.
  */
class DataFlowController @Inject()(configuration: play.api.Configuration, dataFlowDAO: DataFlowDAO, sessionDAO: SessionDAO) extends Controller {
  import models.json.DataFlowDiagramImplicits._

  private lazy val dbTimeout = Duration.fromNanos(configuration.underlying.getInt("tide.db.timeout"))

  def getDataFlow(token: String, id: Option[String]) = Action.async {
    sessionDAO.getUserId(UUID.fromString(token)) map {
      case Some(userId) => id match {
          case Some(diagramId) => // Return one specific diagram
            val dUUID = UUID.fromString(diagramId)
            val diagramOpt = Await.result(dataFlowDAO.getDiagram(userId, dUUID), dbTimeout)

            diagramOpt match {
              case Some(diagram) => // query nodes and links
                val nodes = Await.result(dataFlowDAO.getNodes(dUUID), dbTimeout)
                val links = Await.result(dataFlowDAO.getLinks(dUUID), dbTimeout)
                val result = DFDiagram(
                  dUUID,
                  nodes.map{n => DFNode(n.id, n.category, n.data)},
                  links.map { x => x.from -> x.to }.toMap
                )
                Ok(Json.toJson(result))

              case None =>
                NotFound(s"Diagram $diagramId is not found")
            }

          case None => // List all diagrams
            val ids = Await.result(dataFlowDAO.getDiagrams(userId), dbTimeout)
            Ok(Json.toJson(ids))
        }

      case None => // token doesn't exists
        Unauthorized("Unauthorized")
    }
  }

  def postDataFlow(token: String) = Action.async(parse.json) { request =>
    sessionDAO.getUserId(UUID.fromString(token)) map {
      case Some(user) =>
        request.body.validate[DFDiagram] map { diagram =>
          InternalServerError("Loading is not implemented yet")
        } recoverTotal{ err =>
          ExpectationFailed(Json.toJson("Invalid JSON"))
        }
      case None =>
        Unauthorized("Unauthorized")
    }
  }

  def getCompiledDataFlow(token: String, id: String) = Action.async(parse.json) { request =>
    ???
  }
}
