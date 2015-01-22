package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._
import reactivemongo.bson.{BSONReader, BSONWriter}

/**
 * DeviceAttribute model
 */
case class DeviceAttribute(
    title: String, 
    value: Option[String],
    measure: Option[String],
    isStatic: Option[Int])
		
object DeviceAttribute {
  implicit object DeviceAttributeWriter extends BSONDocumentWriter[DeviceAttribute] {
  	def write(attr: DeviceAttribute): BSONDocument = { 
  	  BSONDocument(
  	      "title"				-> BSONString(attr.title),
  	      "value"				-> attr.value,
  	      "measure"			-> attr.measure,
  	      "isStatic"		-> attr.isStatic)
  	}
  }

  implicit object DeviceAttributeReader extends BSONDocumentReader[DeviceAttribute] {
    def read(doc: BSONDocument): DeviceAttribute = {
		  DeviceAttribute(
		      doc.getAs[BSONString]("title").get.value,
		      doc.getAs[BSONString]("value").map(_.toString()),
		      doc.getAs[BSONString]("measure").map(_.toString()),
		      doc.getAs[BSONInteger]("isStatic").map(_.value))
    }
  }
}

object DeviceAttributeJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val deviceAttributeJsonFormat = jsonFormat4(DeviceAttribute.apply)
}

/**
 * Device model
 */
case class Device(
		_id: Option[BSONObjectID],
		title: String,
		created: DateTime,
		userId: Option[BSONObjectID],
		isPrototype: Option[Boolean],
		attributes: Option[List[DeviceAttribute]],
		programId: Option[BSONObjectID],
		trainingId: Option[BSONObjectID],
		lastTrained: Option[DateTime])
 
object Device {
  
  implicit object DeviceWriter extends BSONDocumentWriter[Device] {
  	def write(device: Device): BSONDocument = {
  	  BSONDocument(
  	      "title"							-> BSONString(device.title),
  	      "created"						-> BSONLong(device.created.getMillis()),
  	      "userId"						-> device.userId,
  	      "isPrototype"				-> device.isPrototype,
  	      "attributes"				-> BSONArray(device.attributes),
  	      "programId"					-> device.programId,
  	      "trainingId"				-> device.trainingId,
  	      "lastTrained"				-> device.lastTrained.map(_.getMillis()))
  	}
  }
  
  implicit object DeviceReader extends BSONDocumentReader[Device] {
    def read(doc: BSONDocument): Device = {
		  Device(
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONString]("title").get.value,
		      doc.getAs[BSONLong]("created").get.value.toDateTime,
		      doc.getAs[BSONObjectID]("userId"),
		      doc.getAs[BSONBoolean]("isPrototype").map(_.value),
		      doc.getAs[List[DeviceAttribute]]("attributes"),
		      doc.getAs[BSONObjectID]("programId"),
		      doc.getAs[BSONObjectID]("trainingId"),
		      doc.getAs[BSONLong]("lastTrained").map(_.value.toDateTime))
    }
  }
}

object DeviceJsonProtocol extends DefaultJsonProtocol {
  import  bp.trainapp.model.DeviceAttributeJsonProtocol._
  import bp.trainapp.model.BaseModel._
  implicit val deviceJsonFormat = jsonFormat9(Device.apply)
}
