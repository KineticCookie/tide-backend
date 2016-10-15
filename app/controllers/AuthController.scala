package controllers

import java.util.UUID
import com.google.inject.Inject
import dao.{SessionDAO, UserDAO}
import models.db.User
import models.json.{UserLoginModel, UserRegistrationModel}
import org.mindrot.jbcrypt.BCrypt
import play.api.mvc.{Action, Controller}
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class AuthController @Inject()(configuration: play.api.Configuration, userDAO: UserDAO, sessionDAO: SessionDAO) extends Controller {
  private lazy val dbTimeout = Duration.fromNanos(configuration.underlying.getInt("tide.db.timeout"))
  private lazy val salt = configuration.underlying.getString("tide.salt")

  /**
    * Type: POST
    *
    * Registers user with given credentials
    */
  val register = Action.async(parse.json) { implicit request =>
    import models.json.UserRegistrationModelImplicits._

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
    }) recoverTotal {
      err => Future(BadRequest("Shit happened"))
    }
  }

  /**
    * Type: POST
    *
    * Creates seesion with user. Returns session id.
    */
  val login = Action.async(parse.json) { implicit request =>
    import models.json.UserLoginModelImplicits._

    request.body.validate[UserLoginModel] map { login =>
      // Check password
      userDAO.getByCredentials(login.email, BCrypt.hashpw(login.pswd, salt)) map {
        case None =>
          BadRequest("Wrong login/password")
        case Some(u) =>

          val token = Await.result(sessionDAO.createToken(u), dbTimeout)
          Ok(token.toString)
      }

    } recoverTotal {
      err => Future(BadRequest("Shit happened"))
    }
  }
}