package bp.trainapp.model

import spray.json._

import reactivemongo.bson._

import org.joda.time.DateTime

abstract class Entity

case class UserClass(email: String, password: Option[String]) extends Entity

case class UserProfileClass(
  firstName: Option[String],
  lastName: Option[String],
  age: Option[Int],
  gender: Option[String],
  height: Option[Double],
  weight: Option[Double]) extends Entity

case class DeviceClass(
  title: String,
  attributes: Option[List[DeviceAttribute]]) extends Entity

case class DeviceUpdateClass(
  id: BSONObjectID,
  title: String,
  userId: Option[BSONObjectID],
  attributes: Option[List[DeviceAttribute]]) extends Entity

/**
 * used to transform request entity when adding new program
 */
case class ProgramClass(title: String, userId: Option[BSONObjectID], devices: Option[List[Device]]) extends Entity

/**
 * used to transform request entity when updating new program
 */
case class ProgramUpdateClass(
  id: BSONObjectID,
  title: String,
  devices: Option[List[Device]],
  isDefault: Option[Int]) extends Entity

/**
 * Providing JsonFormats for Case Classes
 */
object UserClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userClassJsonFormat = jsonFormat2(UserClass)
}

object UserProfileClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userProfileClassJsonFormat = jsonFormat6(UserProfileClass)
}

object DeviceClassJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.DeviceAttributeJsonProtocol._
  implicit val deviceClassJsonFormat = jsonFormat2(DeviceClass)
}

object DeviceUpdateClassJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  import bp.trainapp.model.DeviceAttributeJsonProtocol._
  implicit val deviceUpdateClassJsonFormat = jsonFormat4(DeviceUpdateClass)
}

object ProgramClassJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  import bp.trainapp.model.DeviceJsonProtocol._
  implicit val programClassJsonFormat = jsonFormat3(ProgramClass)
}

object ProgramUpdateClassJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  import bp.trainapp.model.DeviceJsonProtocol._
  implicit val programUpdateClassJsonFormat = jsonFormat4(ProgramUpdateClass)
}