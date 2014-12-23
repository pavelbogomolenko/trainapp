package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class User(
    _id: Option[String],
    firstName: String, 
    lastName: String, 
    age: Double, 
    gender: String,
    height: Double,
    weight: Double,
    registrationDate: Option[String]
    )
 
object User {
	
  implicit object UserWriter extends BSONDocumentWriter[User] {
  	def write(user: User): BSONDocument = BSONDocument(
  	    "firstName" 				-> BSONString(user.firstName),
	      "lastName" 					-> BSONString(user.lastName),
	      "age" 							-> BSONDouble(user.age),
	      "gender" 						-> BSONString(user.gender),
	      "height" 						-> BSONDouble(user.height),
	      "weight"						-> BSONDouble(user.weight),
	      "registrationDate"	-> user.registrationDate.map(date => DateTime.parse(date).getMillis())
	      )
  }
	
	implicit object UserReader extends BSONDocumentReader[User] {
		def read(doc: BSONDocument): User = {  
      User(
        doc.get("_id").map(f => f.toString()),
        doc.getAs[String]("firstName").get,
        doc.getAs[String]("lastName").get,
        doc.getAs[Double]("age").get,
        doc.getAs[String]("gender").get,
        doc.getAs[Double]("height").get,
        doc.getAs[Double]("weight").get,
        doc.getAs[String]("registrationDate").map(dt => new DateTime(dt).toString())
        )
    }
	}
}

object UserJsonProtocol extends DefaultJsonProtocol {
  implicit val userJsonFormat = jsonFormat8(User.apply)
}