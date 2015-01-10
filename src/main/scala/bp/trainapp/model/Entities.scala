package bp.trainapp.model

import spray.json._

case class UserClass(email: String, password: String)
case class UserProfileClass(
    firstName: Option[String], 
		lastName: Option[String], 
		age: Option[Double], 
		gender: Option[String], 
		height: Option[Double], 
		weight: Option[Double])
		
		
object UserClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userClassJsonFormat = jsonFormat2(UserClass)
}

object UserProfileClassJsonProtocol extends DefaultJsonProtocol {
  implicit val userProfileClassJsonFormat = jsonFormat6(UserProfileClass)
}
