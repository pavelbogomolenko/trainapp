package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model.UserSession

class UserSessionRepository(override val db:MongoDbDriver) extends BaseRepository(db) {
    
  val collectionName = "trainapp.usersession"
  
	def list(query:BSONDocument = BSONDocument()): Future[List[UserSession]] = {
	  //getting a list
	  db.collection(collectionName).
	    find(query).
	    cursor[UserSession].
	    collect[List]()
	}
  
  def save(userSession: UserSession) = {
    db.collection(collectionName).insert(userSession)
  }
}