package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.model._

class TrainingRepository extends BaseRepository {

  type Model = Training

  val collectionName = "training"
  
   def save(training: Training) = training match {
    case Training(_id, _, _, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> training._id.get)
      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          "devices" -> training.devices,
          "finish" -> training.finish.map(_.getMillis())))
      update(selector, modifier)
    }
    case Training(None, userId, programid, devices, start, finish) => insert(training)
  }

  def list(): Future[List[Model]] = {
    super.list[Model]()
  }
  
  def findByUserId(userId: BSONObjectID) = {
    val query = BSONDocument(
      "userId" -> userId)
    super.list[Model](query)
  }

  def findByUserIdAndTrainingId(userId: BSONObjectID, trainingId: BSONObjectID) = {
    val query = BSONDocument(
      "userId" -> userId,
      "_id" -> trainingId)
    super.list[Model](query)
  }
  
  def findByUserIdAndProgramId(userId: BSONObjectID, programId: BSONObjectID) = {
    val query = BSONDocument(
      "userId" -> userId,
      "programId" -> programId)
    super.list[Model](query)
  }
  
  /**
   * find last document by user, program
   */
  def findLastUserTraining(userId: BSONObjectID, programId: BSONObjectID) = {
    val query = BSONDocument(
      "userId" -> userId,
      "programId" -> programId)
    val sort = BSONDocument("_id" -> -1)
    super.one[Model](query, sort)
  }
  
  def createFrom(e: Entity, userId: Option[BSONObjectID] = None) = e match {
    case tc: TrainingClass => {
      val training = Training(
        _id = None,
        userId = userId,
        programId = Some(tc.programId),
        devices = tc.devices,
        start = Some(DateTime.now()),
        finish = None)
      save(training)
    }
    case tuc: TrainingUpdateClass => {
      val training = Training(
        _id = Some(tuc.id),
        userId = None,
        programId = None,
        devices = tuc.devices,
        start = None,
        finish = Some(DateTime.now()))
      save(training)
    }
    case _ => throw new Exception("createFrom not implemented for " + e.toString())
  }
}