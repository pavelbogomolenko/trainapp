package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.bson._
import org.joda.time.DateTime

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.service.MongoDbDriver
import bp.trainapp.models.User

trait UserRepository {
  this: DbDriverComponent =>
  
  val collectionName = "trainapp.user"
    
  def list() = {
	  // Select only the documents which field 'firstName' equals 'Jack'
	  //val query = BSONDocument("user_id" -> 1)
	  val query = BSONDocument()

	
	  // Or, the same with getting a list
	  val futureList: Future[List[User]] =
	    db.collection(collectionName).
	    find(query).
	    cursor[User].
	    collect[List]()
	    
	  futureList
	    
//	  futureList.map { list =>
//	  	list.foreach { doc =>
//	  	  doc.age
//	    }
//	  }
  }
}