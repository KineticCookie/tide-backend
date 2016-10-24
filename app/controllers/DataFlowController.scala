package controllers

import java.util.UUID

import com.google.inject.Inject
import dao.{DataFlowDAO, SessionDAO}
import models.ast.{ASTree, PythonGenerator}
import models.db.{DataFlowLink, DataFlowNode}
import models.json.{DFDiagram, DFNode}
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

/**
  * Created by Bulat on 13.09.2016.
  */
class DataFlowController @Inject()(configuration: play.api.Configuration, dataFlowDAO: DataFlowDAO, sessionDAO: SessionDAO) extends Controller {
  import models.json.DataFlowDiagram._

  private lazy val dbTimeout = Duration.fromNanos(configuration.underlying.getInt("tide.db.timeout"))

  def getDataFlow(token: String, id: Option[String]) = Action.async {
    sessionDAO.getUserId(UUID.fromString(token)) map {
      case Some(userId) => id match {
          case Some(diagramId) => // Return one specific diagram
            val dUUID = UUID.fromString(diagramId)
            val diagramOpt = Await.result(dataFlowDAO.getDiagram(dUUID), dbTimeout)

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
          val nodes = diagram.nodes.map(x => DataFlowNode(diagram.id, x.id, x.category, x.data))
          val links = diagram.links.map(x => DataFlowLink(diagram.id, from = x._1, to = x._2)).toSeq
          dataFlowDAO.insertDiagram(user, diagram.id, nodes, links)
          Ok("Added")
        } recoverTotal{ err =>
          ExpectationFailed(Json.toJson("Invalid JSON tree"))
        }
      case None =>
        Unauthorized("Unauthorized")
    }
  }

  def getCompiledDataFlow(token: String, id: String) = Action.async { request =>
    sessionDAO.getUserId(UUID.fromString(token)) map {
      case Some(user) =>
        val dUUID = UUID.fromString(id)
        val diagramOpt = Await.result(dataFlowDAO.getDiagram(dUUID), dbTimeout)

        diagramOpt match {
          case Some(diagram) =>
            val nodes = Await.result(dataFlowDAO.getNodes(dUUID), dbTimeout)
            val links = Await.result(dataFlowDAO.getLinks(dUUID), dbTimeout)
            val ast = ASTree.toAst(diagram.diagramId, nodes, links)
            ast match {
              case Success(tree) =>
                val pythonCodeGen = new PythonGenerator
                pythonCodeGen.visit(tree.root)
                Ok(pythonCodeGen.toSourceCode)
              case Failure(err) =>
                InternalServerError(s"Invalid AST-tree: ${err.toString}")
            }

          case None =>
            NotFound("Diagram is not found")
        }
      case None =>
        Unauthorized("Unauthorized")
    }
  }
}
