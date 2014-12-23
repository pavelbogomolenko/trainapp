package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.model.User

trait UserRepository {
  this: DbDriverComponent =>
  
  val collectionName = "trainapp.user"
    
  def list(): Future[List[User]] = {
	  //val query = BSONDocument("user_id" -> 1)
	  val query = BSONDocument()

	  //getting a list
	  db.collection(collectionName).
	    find(query).
	    cursor[User].
	    collect[List]()
  }
}