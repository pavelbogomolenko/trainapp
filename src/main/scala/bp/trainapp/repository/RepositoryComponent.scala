package bp.trainapp.repository

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model._

trait RepositoryComponent {
	this: DbDriverComponent =>
	
	lazy val userProfileRepository = new UserProfileRepository[UserProfile](db)
	lazy val userRepository = new UserRepository[User](db)
	lazy val userSessionRepository = new UserSessionRepository[UserSession](db)
}

abstract class BaseRepository[T](val db:MongoDbDriver) { 
  val collectionName:String
  
  def list[T](query:BSONDocument = BSONDocument())(implicit reader:BSONDocumentReader[T]) = {
		db.collection(collectionName).
			find(query).
			cursor[T].
			collect[List]()
  }
}