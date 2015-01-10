package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime

case class UserClass(email: String, password: String)

case class UserProfileClass(
    firstName: Option[String], 
		lastName: Option[String], 
		age: Option[Double], 
		gender: Option[String], 
		height: Option[Double], 
		weight: Option[Double])
		
case class DeviceClass(
    title: String,
    attributes: Option[List[DeviceAttribute]])
		
		
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