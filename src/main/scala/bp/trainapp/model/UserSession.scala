package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class UserSession(
		_id: Option[String],
		userId: BSONObjectID,
		sessionId: String,
		ip: Option[String],
		updated: String,
		expired: Option[String])
 
object UserSession {
  
  implicit object UserSessionWriter extends BSONDocumentWriter[UserSession] {
  	def write(userSession: UserSession): BSONDocument = { 
  	  BSONDocument(
  	      "userId"		-> userSession.userId,
  	      "sessionId"	-> BSONString(userSession.sessionId),
  	      "ip"				-> userSession.ip,
  	      "updated"		-> BSONLong(DateTime.parse(userSession.updated).getMillis()),
  	      "expired" 	-> userSession.expired.map(DateTime.parse(_).getMillis()))
  	}
  }
  
  implicit object UserSessionReader extends BSONDocumentReader[UserSession] {
    def read(doc: BSONDocument): UserSession = {
		  UserSession(
		      doc.get("_id").map(f => f.toString()),
		      doc.getAs[BSONObjectID]("userId").get,
		      doc.getAs[BSONString]("sessionId").get.value,
		      doc.getAs[BSONString]("ip").map(_.toString()),
		      doc.getAs[BSONLong]("updated").get.value.toDateTime.toString(),
		      doc.getAs[BSONLong]("expired").map(_.toString().toDateTime.toString())) 
    }
  }
}

object UserSessionJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val userSessionJsonFormat = jsonFormat6(UserSession.apply)
}