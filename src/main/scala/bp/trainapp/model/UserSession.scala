package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class UserSession(
		_id: Option[String],
		userId: String,
		sessionId: String,
		ip: Option[String],
		updated: String
    )
 
object UserSession {
  
  implicit object UserSessionWriter extends BSONDocumentWriter[UserSession] {
  	def write(userSession: UserSession): BSONDocument = { 
  	  BSONDocument(
  	      "userId"		-> BSONObjectID(userSession.userId),
  	      "sessionId"	-> BSONString(userSession.sessionId),
  	      "ip"				-> userSession.ip.getOrElse(""),
  	      "updated"		-> BSONLong(DateTime.parse(userSession.updated).getMillis()))
  	}
  }
  
  implicit object UserSessionReader extends BSONDocumentReader[UserSession] {
    def read(doc: BSONDocument): UserSession = {
		  UserSession(
		      doc.get("_id").map(f => f.toString()),
		      doc.getAs[BSONString]("userId").get.value,
		      doc.getAs[BSONString]("sessionId").get.value,
		      doc.getAs[BSONString]("ip").map(f => f.toString()),
		      doc.getAs[BSONLong]("updated").get.value.toDateTime.toString()) 
    }
  }
}

object UserSessionJsonProtocol extends DefaultJsonProtocol {
  implicit val userSessionJsonFormat = jsonFormat5(UserSession.apply)
}