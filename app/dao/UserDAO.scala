package dao

import scala.concurrent.Future
import javax.inject.Inject

import models.Person
import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
/**
  * Created by Bulat on 11.09.2016.
  */
class PersonDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Persons = TableQuery[PersonsTable]

  def all(): Future[Seq[Person]] = db.run(Persons.result)

  def insert(person: Person): Future[Unit] = db.run(Persons += person).map { _ => () }

  def getByKey(id: Int): Future[Option[Person]] = {
    val q = Persons.filter(_.id === id)
    db.run(q.result) map {x => x.headOption}
  }

  private class PersonsTable(tag: Tag) extends Table[Person](tag, "persons") {

    def id = column[Int]("id", O.PrimaryKey)
    def name = column[String]("name")
    def surname = column[String]("surname")

    override def * : ProvenShape[Person] = (name, surname) <> (Person.tupled, Person.unapply)
  }
}