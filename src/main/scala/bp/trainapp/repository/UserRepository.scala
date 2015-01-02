package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._
import reactivemongo.core.commands.Count

import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model.User

class UserRepository(override val db:MongoDbDriver) extends BaseRepository(db) {
    
  val collectionName = "trainapp.user"
  
	def list(): Future[List[User]] = {
	  val query = BSONDocument()
	
	  //getting a list
	  db.collection(collectionName).
	    find(query).
	    cursor[User].
	    collect[List]()
	}
  
  def save(user: User) = {   
    user match {
      case User(_id, _, _, _) if _id != None => {
        val selector = BSONDocument("_id" -> user._id)
        val modifier = BSONDocument(
        "$set" -> BSONDocument(
        		"login" -> user.email,
        		"password" -> user.password))  
        db.collection(collectionName).update(selector, modifier)
        user
      }
      case User(None, email, password, created) => {
        val futureCount = countByLogin(email)
        val f = futureCount.map { count =>
        	if(count == 0) { 
        	  db.collection(collectionName).insert(user)
        	  user
        	} else {
        	  Future.failed[String](new UserExistsException("user exists"))
        	}
        }
        f
      }
    }
  }
  
  def countByCredentials(login: String, password: String) = {
    val query = BSONDocument("email" -> login, "password" -> password)
		db.connect.command(Count(collectionName, Some(query)))
	}
  
  def countByLogin(login: String) = {
    val query = BSONDocument("email" -> login)
		db.connect.command(Count(collectionName, Some(query)))
	}
}

class UserNotFoundException(message: String) extends Exception(message)
class UserExistsException(message: String) extends Exception(message)