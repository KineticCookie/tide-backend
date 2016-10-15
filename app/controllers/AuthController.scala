package controllers

import java.util.UUID

import com.google.inject.Inject
import dao.{SessionDAO, UserDAO}
import models.{LoginSession, User, UserLoginModel, UserRegistrationModel}
import org.mindrot.jbcrypt.BCrypt
import play.api.data.Forms._
import play.api.data._
import play.api.mvc.{Action, Controller, Cookie}
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesApi

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import play.api.libs.json._

class HomeController @Inject()(userDAO: UserDAO, sessionDAO: SessionDAO, val messagesApi: MessagesApi) extends Controller with I18nSupport {
  // POST
  private val salt = "$2a$10$dbQXaZb0g0YxvldxmPb8xu"

  val register = Action.async(parse.json) { implicit request =>
    import models.UserRegistrationModelImplicits._

    val data = request.body
    data.validate[UserRegistrationModel] map (reg => {
      val newUser = User(
        id = UUID.randomUUID(),
        email = reg.email,
        fullname = reg.fullname,
        pswd = BCrypt.hashpw(reg.pswd, salt)
      )
      userDAO.insert(newUser) map {
        case Success(s) => Ok(newUser.id.toString)
        case Failure(ex) => BadRequest("Cannot create user")
      }
    }) recoverTotal{
      err => Future(BadRequest("Shit happened"))
    }
  }

  // POST
  val login = Action.async(parse.json) { implicit request =>
    import models.UserLoginModelImplicits._

      request.body.validate[UserLoginModel] map { login =>
        // Check password
        userDAO.getByCredentials(login.email, BCrypt.hashpw(login.pswd, salt)) map {
          case None =>
            BadRequest("Wrong login/password")
          case Some(u) =>

            val token = Await.result(sessionDAO.getToken(u), Duration.fromNanos(10000000))
            Ok(token.toString)
        }
    } recoverTotal {
      err => Future(BadRequest("Shit happened"))
    }
  }

  // GET
  def getPersons = Action.async {
    userDAO.all().map(person => Ok(Json.toJson(person.map(p => p.email))))
  }

  // GET
  def getPerson(email: String) = Action.async {
    userDAO.getByEmail(email).map {
      case Some(p) => Ok(Json.toJson(p.email))
      case None => NotFound(Json.toJson("User is not found"))
    }
  }
}