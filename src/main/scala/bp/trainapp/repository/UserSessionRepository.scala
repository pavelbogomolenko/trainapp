package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.{ MongoDbDriver, MongoDbDriverException }
import bp.trainapp.model.UserSession

class UserSessionRepository extends BaseRepository {

  type Model = UserSession

  val collectionName = "usersession"

  def list(): Future[List[Model]] = {
    super.list[Model]()
  }

  def findOneBySesseionId(sessionId: String): Future[Serializable] = {
    val query = BSONDocument("sessionId" -> sessionId)
    super.list[Model](query) map {
      case List(futureSession)  => futureSession
      case _                    => None
    }
  }

  def findValidSession(sessionId: String, sessionLifetime: Long): Future[List[Model]] = {
    val query = BSONDocument(
      "sessionId" -> sessionId,
      "updated" -> BSONDocument("$gt" -> (DateTime.now.getMillis() - sessionLifetime)),
      "expired" -> BSONDocument("$exists" -> false))
    super.list[Model](query)
  }

  def markAsExpired(userSession: Future[UserSession]) = {
    userSession map { u =>
      val selector = BSONDocument("_id" -> u._id.get)
      val modifier = BSONDocument(
        "$set" -> BSONDocument("expired" -> BSONLong(DateTime.now.getMillis())))

      update(selector, modifier)
    }
  }
}