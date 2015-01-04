package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model.UserProfile

class UserProfileRepository[T](override val db:MongoDbDriver) extends BaseRepository(db) {
    
  val collectionName = "trainapp.userprofile"
}