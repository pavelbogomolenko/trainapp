package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.model.UserProfile

trait UserProfileRepository {
  this: DbDriverComponent =>
    
  val collectionName = "trainapp.userprofile"
  
	def list(): Future[List[UserProfile]] = {
	  val query = BSONDocument()
	
	  //getting a list
	  db.collection(collectionName).
	    find(query).
	    cursor[UserProfile].
	    collect[List]()
	}
}