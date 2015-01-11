package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.{MongoDbDriver, MongoDbDriverException} 
import bp.trainapp.model.UserSession

class UserSessionRepository[T](override val db:MongoDbDriver) extends BaseRepository(db) {
    
  val collectionName = "trainapp.usersession"
  
  def save(userSession: UserSession) = {
    db.collection(collectionName).insert(userSession)
  }
  
  def findBySesseionId[T](sessionId: String)(implicit reader:BSONDocumentReader[T]) = {
    val query = BSONDocument("sessionId" -> sessionId)
    list[T](query)
	}
  
  def findValidSession[T](sessionId: String, sessionLifetime: Long)(implicit reader:BSONDocumentReader[T]) = {
    val query = BSONDocument(
        "sessionId"	-> sessionId,
        "updated"		-> BSONDocument("$gt"			-> (DateTime.now.getMillis() - sessionLifetime)),
        "expired"		-> BSONDocument("$exists" -> false))
    list[T](query)
  }
  
  def markAsExpired(userSession: Future[UserSession]) = {
    userSession map  { u =>
	    val selector = BSONDocument("_id" -> u._id.get)
	    val modifier = BSONDocument(
	        "$set" -> BSONDocument("expired" -> BSONLong(DateTime.now.getMillis())))
	      
	    val res = db.collection(collectionName).update(selector, modifier)
	    res.onComplete {
	      case Success(result)  => result
	      case Failure(failure) => throw new MongoDbDriverException(failure.getMessage())
	    }
    }
  }
}