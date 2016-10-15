package dao

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import models.db.{LoginSession, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future

/**
  * Created by Bulat on 11.09.2016.
  */
class SessionDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Sessions = TableQuery[SessionsTable]

  def all(): Future[Seq[LoginSession]] = db.run(Sessions.result)

  def createToken(person: User): Future[UUID] = {
    val uuid = UUID.randomUUID()
    val expires = LocalDateTime.now().plusHours(1)
    db.run(Sessions += LoginSession(person.id, uuid, expires))
    Future(uuid)
  }

  def getUserId(token: UUID): Future[Option[UUID]] = {
    val query = Sessions.filter(_.token === token).result.headOption.map {
      case None =>
        None
      case Some(x) =>
        Some(x.user_id)
    }
    db.run(query)
  }

  private class SessionsTable(tag: Tag) extends Table[LoginSession](tag, "sessions") {
    implicit val localDateTimeToTimeStamp = MappedColumnType.base[LocalDateTime, Timestamp](
      l => Timestamp.valueOf(l),
      d => d.toLocalDateTime
    )

    def id = column[UUID]("id", O.PrimaryKey)
    def token = column[UUID]("token")
    def expires = column[LocalDateTime]("expires")

    override def * : ProvenShape[LoginSession] = (id, token, expires) <> (LoginSession.tupled, LoginSession.unapply)

  }

}