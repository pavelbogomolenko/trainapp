package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class UserProfile(
		_id: Option[String],
		userId: Option[String],
		firstName: String, 
		lastName: String, 
		age: Double, 
		gender: String, 
		height: Double, 
		weight: Double, 
		registrationDate: Option[String])
 
object UserProfile {
  
  implicit object UserProfileWriter extends BSONDocumentWriter[UserProfile] {
  	def write(userProfile: UserProfile): BSONDocument = { 
  	  BSONDocument(
  	      "userId"						-> userProfile.userId.map(id => BSONObjectID(id)),
					"firstName"					-> BSONString(userProfile.firstName),
		      "lastName"					-> BSONString(userProfile.lastName),
		      "age"								-> BSONDouble(userProfile.age),
		      "gender"						-> BSONString(userProfile.gender),
		      "height"						-> BSONDouble(userProfile.height),
		      "weight"						-> BSONDouble(userProfile.weight),
		      "registrationDate"	-> userProfile.registrationDate.map(date => DateTime.parse(date).getMillis()))
  	}
  }
  
  implicit object UserProfileReader extends BSONDocumentReader[UserProfile] {
    def read(doc: BSONDocument): UserProfile = {
		  UserProfile(
		      doc.get("_id").map(f => f.toString()),
		      doc.getAs[String]("userId").map(f => f.toString()),
		      doc.getAs[String]("firstName").get,
		      doc.getAs[String]("lastName").get,
		      doc.getAs[Double]("age").get,
		      doc.getAs[String]("gender").get,
		      doc.getAs[Double]("height").get,
		      doc.getAs[Double]("weight").get,
		      doc.getAs[String]("registrationDate").map(dt => new DateTime(dt).toString()))
    }
  }
}

object UserProfileJsonProtocol extends DefaultJsonProtocol {
  implicit val userProfileJsonFormat = jsonFormat9(UserProfile.apply)
}