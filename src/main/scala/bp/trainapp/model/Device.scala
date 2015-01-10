package bp.trainapp.model

import com.github.nscala_time.time.Imports._
import spray.json._

import org.joda.time.DateTime
import reactivemongo.bson._
import reactivemongo.bson.{BSONReader, BSONWriter}

/**
 * DeviceAttribute model
 */
case class DeviceAttribute(
		title: String,
		value: String,
		created: String)
		
object DeviceAttribute {
  implicit object DeviceAttributeWriter extends BSONDocumentWriter[DeviceAttribute] {
  	def write(attr: DeviceAttribute): BSONDocument = { 
  	  BSONDocument(
  	      "title"							-> BSONString(attr.title),
  	      "value"							-> BSONString(attr.value),
  	      "created"						-> BSONLong(DateTime.parse(attr.created).getMillis()))
  	}
  }

  implicit object DeviceAttributeReader extends BSONDocumentReader[DeviceAttribute] {
    def read(doc: BSONDocument): DeviceAttribute = {
		  DeviceAttribute(
		      doc.getAs[BSONString]("title").get.value,
		      doc.getAs[BSONString]("value").get.value,
		      doc.getAs[BSONLong]("created").get.value.toDateTime.toString())
    }
  }
}

object DeviceAttributeJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val deviceAttributeJsonFormat = jsonFormat3(DeviceAttribute.apply)
}

/**
 * Device model
 */
case class Device(
		_id: Option[BSONObjectID],
		title: String,
		created: String,
		atributes: Option[List[DeviceAttribute]])
 
object Device {
  
  implicit object DeviceWriter extends BSONDocumentWriter[Device] {
  	def write(device: Device): BSONDocument = { 
  	  BSONDocument(
  	      "title"							-> BSONString(device.title),
  	      "created"						-> BSONLong(DateTime.parse(device.created).getMillis()),
  	      "atributes"					-> BSONArray(device.atributes))
  	}
  }
  
  implicit object DeviceReader extends BSONDocumentReader[Device] {
    def read(doc: BSONDocument): Device = {
		  Device(
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONString]("title").get.value,
		      doc.getAs[BSONLong]("created").get.value.toDateTime.toString(),
		      Some(doc.getAs[List[DeviceAttribute]]("atributes").get))
    }
  }
}

object DeviceJsonProtocol extends DefaultJsonProtocol {
  import  bp.trainapp.model.DeviceAttributeJsonProtocol._
  import bp.trainapp.model.BaseModel._
  implicit val deviceJsonFormat = jsonFormat4(Device.apply)
}
