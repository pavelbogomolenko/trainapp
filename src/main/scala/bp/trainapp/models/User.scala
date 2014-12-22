package bp.trainapp.models

import com.github.nscala_time.time.Imports._
import spray.json._
//import spray.json.DefaultJsonProtocol

import org.joda.time.DateTime
import reactivemongo.bson._
//import reactivemongo.bson.BSONDocumentWriter
//import reactivemongo.bson.BSONDocumentReader
//import reactivemongo.bson.BSONDateTime
//import reactivemongo.bson.BSONString
//import reactivemongo.bson.BSONLong
//import reactivemongo.bson.BSONInteger
//import reactivemongo.bson.BSONObjectID
//import reactivemongo.bson.BSONDouble

case class User(
    _id: Option[BSONObjectID],
    firstName: String, 
    lastName: String, 
    age: Double, 
    gender: String,
    height: Double,
    weight: Double,
    registrationDate: Option[DateTime])
 
object User {
  implicit object UserWriter extends BSONDocumentWriter[User] {
  	def write(user: User): BSONDocument = BSONDocument(
  	    "_id" 							-> user._id.getOrElse(BSONObjectID.generate),
  	    "firstName" 				-> BSONString(user.firstName),
	      "lastName" 					-> BSONString(user.lastName),
	      "age" 							-> BSONDouble(user.age),
	      "gender" 						-> BSONString(user.gender),
	      "height" 						-> BSONDouble(user.height),
	      "weight"						-> BSONDouble(user.weight),
	      "registrationDate"	-> user.registrationDate.map(date => BSONDateTime(date.getMillis())))
  }
	
	implicit object UserReader extends BSONDocumentReader[User] {
		def read(doc: BSONDocument): User = {
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[String]("firstName").get,
        doc.getAs[String]("lastName").get,
        doc.getAs[Double]("age").get,
        doc.getAs[String]("gender").get,
        doc.getAs[Double]("height").get,
        doc.getAs[Double]("weight").get,
        doc.getAs[BSONDateTime]("registrationDate").map(dt => new DateTime(dt.value))
      )
    }
	}
}

//object UserJsonProtocol extends DefaultJsonProtocol {
//  implicit val userJsonFormat = jsonFormat8(User.apply)
//}

//object MyJsonProtocol extends DefaultJsonProtocol {
//  implicit object UserJsonFormat extends RootJsonFormat[User] {
//    def write(u: User) = {
//      JsArray(
//          JsString(u.firstName),
//          JsString(u.lastName), 
//          JsNumber(u.age))
//    }
//  }
//}