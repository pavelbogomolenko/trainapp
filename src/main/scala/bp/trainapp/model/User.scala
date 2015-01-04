package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class User(
		_id: Option[BSONObjectID],
		email: String,
		password: String,
		created: String
    )
 
object User {
  
  implicit object UserWriter extends BSONDocumentWriter[User] {
  	def write(user: User): BSONDocument = { 
  	  BSONDocument(
  	      "email"							-> BSONString(user.email),
  	      "password"					-> BSONString(user.password),
  	      "created"						-> BSONLong(DateTime.parse(user.created).getMillis()))
  	}
  }
  
  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
		  User(
		      //doc.getAs[BSONObjectID]("_id").map(BSONObjectID.unapply(_).toString()),
		      //Some(BSONObjectID.unapply(doc.getAs[BSONObjectID]("_id").get).get),
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONString]("email").get.value,
		      doc.getAs[BSONString]("password").get.value,
		      doc.getAs[BSONLong]("created").get.value.toDateTime.toString())
    }
  }
}

object UserJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val userJsonFormat = jsonFormat4(User.apply)
}