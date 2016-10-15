package dao

import java.util.UUID
import javax.inject.Inject
import models.db.{DataFlowNode, DataFlowLink, UserToDiagram}
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

  def getDiagram(userId: UUID, diagramId: UUID): Future[Option[UserToDiagram]] = db.run(
    Diagrams.filter { x =>
      x.userId === userId && x.diagramId === diagramId
    }.result.headOption
  )

  def getNodes(diagramId: UUID): Future[Seq[DataFlowNode]] = db.run(
    Nodes.filter(_.diagramId === diagramId).result
  )

  def getLinks(diagramId: UUID): Future[Seq[DataFlowLink]] = db.run(
    Links.filter(_.diagramId === diagramId).result
  )

  def insertDiagram(diagramId: UUID, nodes: Seq[DataFlowNode], links: Seq[DataFlowLink]) =
    ???

  private class DFUsers(tag: Tag) extends Table[UserToDiagram](tag, "data_diagrams") {
    def userId = column[UUID]("userId", O.PrimaryKey)
    def diagramId = column[UUID]("diagramId", O.PrimaryKey)

    override def * = (userId, diagramId) <> (UserToDiagram.tupled, UserToDiagram.unapply)
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