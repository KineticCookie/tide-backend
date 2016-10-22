package dao

import java.util.UUID
import javax.inject.Inject

import models.db.{DataFlowLink, DataFlowNode, Diagram}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Created by Bulat on 03.10.2016.
  */

// TODO limit output
class DataFlowDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Diagrams = TableQuery[DFUsers]
  private val Nodes = TableQuery[DFNodes]
  private val Links = TableQuery[DFLinks]

  def getDiagrams(userId: UUID): Future[Seq[UUID]] = db.run(
    Diagrams.filter(_.userId === userId).map(_.diagramId).result
  )

  def getDiagram(diagramId: UUID): Future[Option[Diagram]] = db.run(
    Diagrams.filter { _.diagramId === diagramId}.result.headOption
  )

  def getNodes(diagramId: UUID): Future[Seq[DataFlowNode]] = db.run(
    Nodes.filter(_.diagramId === diagramId).result
  )

  def getLinks(diagramId: UUID): Future[Seq[DataFlowLink]] = db.run(
    Links.filter(_.diagramId === diagramId).result
  )

  def insertDiagram(userId: UUID,
                    diagramId: UUID,
                    nodes: Seq[DataFlowNode],
                    links: Seq[DataFlowLink]) = {
    import scala.concurrent._
    import ExecutionContext.Implicits.global
    val res = db.run(Diagrams.filter {
        x => x.userId === userId && x.diagramId === diagramId
      }.result.headOption)
    res.map {
      case Some(u2d) => // Remove old tree
        db.run(Nodes.filter {
          _.diagramId === diagramId
        }.delete)
        db.run(Links.filter {
          _.diagramId === diagramId
        }.delete)
      case None => // New tree - add entry
        db.run(Diagrams += Diagram(userId, diagramId))
    }
    db.run(Nodes ++= nodes)
    db.run(Links ++= links)
  }

  private class DFUsers(tag: Tag) extends Table[Diagram](tag, "data_diagrams") {
    def userId = column[UUID]("userId", O.PrimaryKey)
    def diagramId = column[UUID]("diagramId", O.PrimaryKey)

    override def * = (userId, diagramId) <> (Diagram.tupled, Diagram.unapply)
  }

  private class DFNodes(tag: Tag) extends Table[DataFlowNode](tag, "data_nodes") {
    def diagramId = column[UUID]("diagramId", O.PrimaryKey)
    def id = column[UUID]("id", O.PrimaryKey)
    def category = column[String]("category")
    def data = column[String]("data")

    override def * = (diagramId, id, category, data) <> (DataFlowNode.tupled, DataFlowNode.unapply)
  }

  private class DFLinks(tag: Tag) extends Table[DataFlowLink](tag, "data_links") {
    def diagramId = column[UUID]("diagramId", O.PrimaryKey)
    def from = column[UUID]("from", O.PrimaryKey)
    def to = column[UUID]("to", O.PrimaryKey)

    override def * = (diagramId, from, to) <> (DataFlowLink.tupled, DataFlowLink.unapply)
  }

}