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
    _id: Option[BSONObjectID],
		title: String,
		created: String)
		
object DeviceAttribute {
  implicit object DeviceAttributeWriter extends BSONDocumentWriter[DeviceAttribute] {
  	def write(attr: DeviceAttribute): BSONDocument = { 
  	  BSONDocument(
  	      "title"							-> BSONString(attr.title),
  	      "created"						-> BSONLong(DateTime.parse(attr.created).getMillis()))
  	}
  }

  implicit object DeviceAttributeReader extends BSONDocumentReader[DeviceAttribute] {
    def read(doc: BSONDocument): DeviceAttribute = {
		  DeviceAttribute(
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONString]("title").get.value,
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
		atributes: Option[List[BSONObjectID]])
 
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
		      Some(doc.getAs[List[BSONObjectID]]("atributes").get))
    }
  }
}

object DeviceJsonProtocol extends DefaultJsonProtocol {
  import  bp.trainapp.model.DeviceAttributeJsonProtocol._
  import bp.trainapp.model.BaseModel._
  implicit val deviceJsonFormat = jsonFormat4(Device.apply)
}

/**
 * DeviceAttributeValue model
 * Hold relation between device and attribute values
 */
case class DeviceAttributeValue(
    _id: Option[BSONObjectID],
    deviceId: BSONObjectID,
    attributeId: BSONObjectID,
    value: String)
    
object DeviceAttributeValue {
	implicit object DeviceAttributeValueWriter extends BSONDocumentWriter[DeviceAttributeValue] {
  	def write(attrValue: DeviceAttributeValue): BSONDocument = { 
  	  BSONDocument(
  	      "deviceId"					-> attrValue.deviceId,
  	      "attributeId"				-> attrValue.attributeId,
  	      "value"							-> BSONString(attrValue.value))
  	}
  }

  implicit object DeviceAttributeValueReader extends BSONDocumentReader[DeviceAttributeValue] {
    def read(doc: BSONDocument): DeviceAttributeValue = {
		  DeviceAttributeValue(
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONObjectID]("deviceId").get,
		      doc.getAs[BSONObjectID]("attributeId").get,
		      doc.getAs[BSONString]("value").get.value)
    }
  }
}
    
object DeviceAttributeValueJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val deviceAttributeValueJsonFormat = jsonFormat4(DeviceAttributeValue.apply)
}
