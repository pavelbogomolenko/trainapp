package bp.trainapp.repository

import scala.util.{Try, Success, Failure}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.Count

import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model.User
import bp.trainapp.model.UserClass

class UserRepository[T](override val db:MongoDbDriver) extends BaseRepository(db) {
    
  val collectionName = db.dbName + "." + "user"
  
  def save(user: User) = user match {
    case User(_id, _, _, _, _, _, _, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> user._id.get)
      val modifier = BSONDocument(
      "$set" -> BSONDocument(
      		"login" -> user.email,
      		"password" -> user.password))  
      update(selector, modifier)
    }
    case User(None, email, password, firstName, lastName, age, gender, height, weight, created) => {
      val found = findByLogin(email) map {
        case Nil => insert(user)
        case List(a) => throw new UserExistsException("user exists")
      }
      found
    }
  }
  
  def findByCredentials[T](login: String, password: String)(implicit reader:BSONDocumentReader[T]) = {
    val query = BSONDocument("email" -> login, "password" -> password)
		list[T](query)
	}
  
  def findByLogin[T](login: String)(implicit reader:BSONDocumentReader[T]) = {
    val query = BSONDocument("email" -> login)
    list[T](query)
	}
  
  def createFromUserClass(u: UserClass) = {
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