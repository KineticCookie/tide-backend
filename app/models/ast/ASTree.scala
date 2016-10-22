package models.ast

import java.util.UUID

import models.db.{DataFlowLink, DataFlowNode}

import scala.util.{Failure, Success, Try}

/**
  * Created by Bulat on 15.10.2016.
  */
case class ASTree(id: UUID, root: ASTNode)

object ASTree {
  def toAst(id: UUID, nodes: Seq[DataFlowNode], links: Seq[DataFlowLink]): Try[ASTree] =
    findRoot(nodes, links).flatMap { uuid =>
      buildSubTree(nodes, links, uuid) map { tree =>
        ASTree(id, tree)
      }
    }

  private def findRoot(nodes: Seq[DataFlowNode], links: Seq[DataFlowLink]): Try[UUID] = {
    val ids = nodes map {_.id}
    val fromIds = links.map(_.from)
    val roots = ids filter {x => !fromIds.contains(x) }

    roots match {
      case head::Nil =>
        Success(head)
      case _ =>
        Failure(new IllegalArgumentException("Invalid tree"))
    }
  }

  private def buildSubTree(nodes: Seq[DataFlowNode], links: Seq[DataFlowLink], rootId: UUID): Try[ASTNode] = Try({
    val node = nodes find {
      _.id == rootId
    } get
    val children = links.filter {
      _.to == rootId
    }.map { x => buildSubTree(nodes, links, x.from).get }

    node.category match {
      case "DataNode" =>
        new DataNode(node.id, node.data, children)

      case "MapNode" =>
        new MapNode(node.id ,node.data, children)

      case "ReduceNode" =>
        new ReduceNode(node.id, node.data, children)

      case x => throw new IllegalArgumentException(s"Invalid json node: $x")
    }
  })
}