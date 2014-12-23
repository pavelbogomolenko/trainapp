package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class UserSession(
		_id: Option[String],
		userId: Option[String],
		session: String,
		ip: String,
		updated: Option[String]
    )
 
object UserSession {
  
  implicit object UserSessionWriter extends BSONDocumentWriter[UserSession] {
  	def write(userSession: UserSession): BSONDocument = { 
  	  BSONDocument(
  	      "userId"	-> userSession.userId.map(id => BSONObjectID(id)),
  	      "session"	-> BSONString(userSession.session),
  	      "ip"			-> BSONString(userSession.ip),
  	      "updated"	-> userSession.updated.map(date => DateTime.parse(date).getMillis()))
  	}
  }
  
  implicit object UserSessionReader extends BSONDocumentReader[UserSession] {
    def read(doc: BSONDocument): UserSession = {
		  UserSession(
		      doc.get("_id").map(f => f.toString()),
		      doc.getAs[String]("userId").map(f => f.toString()),
		      doc.getAs[String]("session").get,
		      doc.getAs[String]("ip").get,
		      doc.getAs[String]("updated").map(dt => new DateTime(dt).toString()))
    }
  }
}

object UserSessionJsonProtocol extends DefaultJsonProtocol {
  implicit val userSessionJsonFormat = jsonFormat5(UserSession.apply)
}