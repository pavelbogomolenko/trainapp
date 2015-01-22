package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.model.Training

class TrainingRepository extends BaseRepository {
    
  type Model = Training
  
  val collectionName = "training"
  
  def list(): Future[List[Model]] = {
    super.list[Model]()
	}
  
  def findByUserId(userId: BSONObjectID) = {
    val query = BSONDocument("userId" -> userId)
    super.list[Model](query)
  }
  
  def findByUserIdAndTrainingId(userId: BSONObjectID, trainingId: BSONObjectID) = {
    val query = BSONDocument(
        "userId" -> userId,
        "trainingId" -> trainingId)
    super.list[Model](query)
  }
}