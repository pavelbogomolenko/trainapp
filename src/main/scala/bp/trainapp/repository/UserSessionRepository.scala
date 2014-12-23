package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.model.UserSession

trait UserSessionRepository {
  this: DbDriverComponent =>
    
  val collectionName = "trainapp.usersession"
  
	def list(): Future[List[UserSession]] = {
	  val query = BSONDocument()
	
	  //getting a list
	  db.collection(collectionName).
	    find(query).
	    cursor[UserSession].
	    collect[List]()
	}
}