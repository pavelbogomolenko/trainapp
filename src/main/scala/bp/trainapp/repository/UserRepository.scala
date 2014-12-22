package bp.trainapp.repository

import reactivemongo.api._
import reactivemongo.bson._
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

import bp.trainapp.service.MongoDbDriver

import bp.trainapp.models.User

class UserRepository {
  val db: MongoDbDriver
  val entity: User
  
  def listDocs() = {
	  // Select only the documents which field 'firstName' equals 'Jack'
	  val query = BSONDocument("firstName" -> "Jack")
	
	  // Or, the same with getting a list
	  val futureList: Future[List[BSONDocument]] =
	    db.collection("user").
	      find(query).
	      cursor[BSONDocument].
	      collect[List]()
	
	  futureList.map { list =>
	  	list.foreach { doc =>
	      println("found document: " + BSONDocument.pretty(doc))
	    }
	  }
  }
}