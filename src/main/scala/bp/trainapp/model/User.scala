package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._

case class User(
  _id: Option[BSONObjectID],
  email: String,
  password: Option[String],
  firstName: Option[String],
  lastName: Option[String],
  age: Option[Int],
  gender: Option[String],
  height: Option[Double],
  weight: Option[Double],
  created: DateTime)

object User {

  implicit object UserWriter extends BSONDocumentWriter[User] {
    def write(user: User): BSONDocument = {
      BSONDocument(
        "email" -> BSONString(user.email),
        "password" -> user.password,
        "firstName" -> user.firstName,
        "lastName" -> user.lastName,
        "age" -> user.age,
        "gender" -> user.gender,
        "height" -> user.height,
        "weight" -> user.weight,
        "created" -> BSONLong(user.created.getMillis()))
    }
  }

  implicit object UserReader extends BSONDocumentReader[User] {
    def read(doc: BSONDocument): User = {
      User(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("email").get.value,
        doc.getAs[BSONString]("password").map(_.toString),
        doc.getAs[String]("firstName"),
        doc.getAs[String]("lastName"),
        doc.getAs[Int]("age"),
        doc.getAs[String]("gender"),
        doc.getAs[Double]("height"),
        doc.getAs[Double]("weight"),
        doc.getAs[BSONLong]("created").get.value.toDateTime)
    }
  }
}

object UserJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val userJsonFormat = jsonFormat10(User.apply)
}