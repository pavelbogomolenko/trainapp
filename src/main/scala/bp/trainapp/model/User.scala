package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class User(
		_id: Option[String],
		email: String,
		password: String,
		created: Option[String] = Some(DateTime.now().toString())
    )
 
object User {
  
  implicit object UserWriter extends BSONDocumentWriter[User] {
  	def write(user: User): BSONDocument = { 
  	  BSONDocument(
  	      "email"							-> BSONString(user.email),
  	      "password"					-> BSONString(user.password),
  	      "created"						-> user.created.map(date => DateTime.parse(date).getMillis()))
  	}
  }
  
  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
		  User(
		      doc.get("_id").map(f => f.toString()),
		      doc.getAs[String]("email").get,
		      doc.getAs[String]("password").get,
		      doc.getAs[String]("created").map(dt => new DateTime(dt).toString()))
    }
  }
}

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userJsonFormat = jsonFormat4(User.apply)
}