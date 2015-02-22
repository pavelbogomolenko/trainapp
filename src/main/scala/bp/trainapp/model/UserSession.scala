package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._

case class UserSession(
  _id: Option[BSONObjectID],
  userId: BSONObjectID,
  sessionId: String,
  ip: Option[String],
  updated: DateTime,
  expired: Option[DateTime])

object UserSession {

  implicit object UserSessionWriter extends BSONDocumentWriter[UserSession] {
    def write(userSession: UserSession): BSONDocument = {
      BSONDocument(
        "userId" -> userSession.userId,
        "sessionId" -> BSONString(userSession.sessionId),
        "ip" -> userSession.ip,
        "updated" -> userSession.updated.getMillis(),
        "expired" -> userSession.expired.map(_.getMillis()))
    }
  }

  implicit object UserSessionReader extends BSONDocumentReader[UserSession] {
    def read(doc: BSONDocument): UserSession = {
      UserSession(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONObjectID]("userId").get,
        doc.getAs[BSONString]("sessionId").get.value,
        doc.getAs[BSONString]("ip").map(_.toString()),
        doc.getAs[BSONLong]("updated").get.value.toDateTime,
        doc.getAs[BSONLong]("expired").map(_.value.toDateTime))
    }
  }
}

object UserSessionJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val userSessionJsonFormat = jsonFormat6(UserSession.apply)
}