package bp.trainapp.models

import com.github.nscala_time.time.Imports._
import spray.json._
import DefaultJsonProtocol._

sealed class Gender
case object Male extends Gender {
  override def toString = "m"
}
case object Female extends Gender {
  override def toString = "f"
}

case class User(
    id: Option[Long],
    firstName: String, 
    lastName: String, 
    age: Int, 
    gender: Gender,
    height: Float,
    weight: Float,
    registrationDate: DateTime) extends AbstractEntity {
 
}