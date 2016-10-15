package dao

import java.util.UUID
import javax.inject.Inject
import models.db.User
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

/**
  * Created by Bulat on 11.09.2016.
  */
class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  private val Users = TableQuery[UsersTable]

  def all(): Future[Seq[User]] = db.run(Users.result)

  def insert(person: User): Future[Try[String]] = {
    val query = Users.filter(_.email === person.email).exists.result.map {
      case false =>
        val w = Users += person
        db.run(w)
        Success("Added successfully")
      case true =>
        println(s"Person already exists $person")
        Failure(new IllegalArgumentException("User already exists"))
    }
    db.run(query)
  }

  def get(id: UUID): Future[Option[User]] = {
    val q = Users.filter(_.id === id)
    db.run(q.result) map { x => x.headOption }
  }

  def getByEmail(email: String): Future[Option[User]] = {
    val q = Users.filter(_.email === email)
    db.run(q.result) map { x => x.headOption }
  }

  def getByCredentials(email: String, pswd: String): Future[Option[User]] = {
    val q = Users filter (x => x.email === email && x.pswd === pswd)
    db.run(q.result) map { x => x.headOption }
  }

  private class UsersTable(tag: Tag) extends Table[User](tag, "users") {
    def id = column[UUID]("id", O.PrimaryKey)
    def fullname = column[String]("fullname")
    def email = column[String]("email")
    def pswd = column[String]("pswd")

    override def * : ProvenShape[User] = (id, fullname, email, pswd) <> (User.tupled, User.unapply)
  }

}