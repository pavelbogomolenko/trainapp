package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class UserSession(
		_id: Option[String],
		userId: Array[Byte],
		sessionId: String,
		ip: String = "",
		updated: String,
		expired: String = "")
 
object UserSession {
  
  implicit object UserSessionWriter extends BSONDocumentWriter[UserSession] {
  	def write(userSession: UserSession): BSONDocument = { 
  	  println("write")
//  	  BSONDocument(
//  	      "userId"		-> BSONString(userSession.userId),
//  	      "sessionId"	-> BSONString(userSession.sessionId),
//  	      "ip"				-> BSONString(userSession.ip),
//  	      "updated"		-> BSONLong(DateTime.parse(userSession.updated).getMillis()),
//  	      "expired" 	-> BSONLong(DateTime.parse(userSession.expired).getMillis()))
  	  BSONDocument("userId"		-> BSONObjectID(userSession.userId))
  	}
  }
  
  implicit object UserSessionReader extends BSONDocumentReader[UserSession] {
    def read(doc: BSONDocument): UserSession = {
      println("read")
		  UserSession(
		      doc.get("_id").map(f => f.toString()),
		      //doc.getAs[BSONObjectID]("userId"),
		      BSONObjectID.unapply(doc.getAs[BSONObjectID]("userId").get).get,
		      doc.getAs[BSONString]("sessionId").get.value,
		      doc.getAs[BSONString]("ip").get.value,
		      doc.getAs[BSONLong]("updated").get.value.toDateTime.toString(),
		      doc.getAs[BSONLong]("expired").get.value.toDateTime.toString()) 
    }
  }
}

object UserSessionJsonProtocol extends DefaultJsonProtocol {
  implicit val userSessionJsonFormat = jsonFormat6(UserSession.apply)
}