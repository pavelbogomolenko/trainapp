package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._

case class DeviceAttribute(
		_id: Option[BSONObjectID],
		title: String,
		value: String,
		created: String)


case class Device(
		_id: Option[BSONObjectID],
		title: String,
		created: String,
		atributes: Option[List[String]])
		
//atributes: Option[List[DeviceAttribute]]
 
object Device {
  
  implicit object UserWriter extends BSONDocumentWriter[Device] {
  	def write(device: Device): BSONDocument = { 
  	  BSONDocument(
  	      "title"							-> BSONString(device.title),
  	      "created"						-> BSONLong(DateTime.parse(device.created).getMillis()),
  	      "atributes"					-> BSONArray(device.atributes))
  	}
  }
  
//  implicit object UserReader extends BSONDocumentReader[Device] {
//    def read(doc: BSONDocument): Device = {
//		  Device(
//		      doc.getAs[BSONObjectID]("_id"),
//		      doc.getAs[BSONString]("title").get.value,
//		      doc.getAs[BSONLong]("created").get.value.toDateTime.toString(),
//		      Some(doc.getAs[BSONArray]("atributes").get.values))
//    }
//  }
}

object DeviceAttributeJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val deviceAttributeJsonFormat = jsonFormat4(DeviceAttribute.apply)
}

object DeviceJsonProtocol extends DefaultJsonProtocol {
  import  bp.trainapp.model.DeviceAttributeJsonProtocol._
  import bp.trainapp.model.BaseModel._
  implicit val deviceJsonFormat = jsonFormat4(Device.apply)
}