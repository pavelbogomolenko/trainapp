package bp.trainapp.model

import spray.json._

import reactivemongo.bson._

import org.joda.time.DateTime

abstract class Entity

case class UserClass(email: String, password: String) extends Entity

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
		
		
object UserClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userClassJsonFormat = jsonFormat2(UserClass)
}

object UserProfileClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userProfileClassJsonFormat = jsonFormat6(UserProfileClass)
}

object DeviceClassJsonProtocol extends DefaultJsonProtocol {
  import	bp.trainapp.model.DeviceAttributeJsonProtocol._
  implicit val deviceClassJsonFormat = jsonFormat2(DeviceClass)
}

object DeviceUpdateClassJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  import	bp.trainapp.model.DeviceAttributeJsonProtocol._
  implicit val deviceUpdateClassJsonFormat = jsonFormat4(DeviceUpdateClass)
}