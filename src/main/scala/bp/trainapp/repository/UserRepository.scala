package bp.trainapp.repository

import scala.util.{ Try, Success, Failure }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.joda.time.DateTime
import reactivemongo.api._
import reactivemongo.bson._
import bp.trainapp.model.User
import bp.trainapp.model.UserClass

class UserRepository extends BaseRepository {

  type Model = User

  val collectionName = "user"

  def save(user: User) = user match {
    case User(_id, _, _, _, _, _, _, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> user._id.get)
      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          "login" -> user.email,
          "password" -> user.password))
      update(selector, modifier)
      //@to-do not that good to return object need proper error handling in terms of error
      user
    }
    case User(None, email, password, firstName, lastName, age, gender, height, weight, created) => {
      val found = findOneByLogin(email) flatMap {
        case user: User => throw new UserExistsException("user exists")
        case _          => insert(user)
      }
      //@to-do not that good to return object need proper error handling in terms of error
      user
    }
  }

  def list(): Future[List[Model]] = {
    super.list[Model]()
  }

  def findByCredentials(login: String, password: String) = {
    val query = BSONDocument("email" -> login, "password" -> password)
    super.list[Model](query)
  }

  def findOneByLogin(login: String) = {
    val query = BSONDocument("email" -> login)
    super.list[Model](query) map {
      case List(futureUser) => futureUser
      case _                => None
    }
  }

  def createFrom(u: UserClass) = {
    val user = User(
      _id = None,
      email = u.email,
      password = u.password,
      firstName = None,
      lastName = None,
      age = None,
      gender = None,
      height = None,
      weight = None,
      created = DateTime.now())
    save(user)
  }
}

class UserNotFoundException(message: String) extends Exception(message)
class UserExistsException(message: String) extends Exception(message)