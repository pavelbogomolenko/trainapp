package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._
import reactivemongo.bson.{ BSONReader, BSONWriter }

/**
 * DeviceAttribute model
 */
case class DeviceAttribute(
  _id: Option[BSONObjectID],
  title: String,
  value: Option[String],
  measure: Option[String],
  isStatic: Option[Int])

object DeviceAttribute {
  implicit object DeviceAttributeWriter extends BSONDocumentWriter[DeviceAttribute] {
    def write(attr: DeviceAttribute): BSONDocument = {
      BSONDocument(
        "title" -> attr.title,
        "value" -> attr.value,
        "measure" -> attr.measure,
        "isStatic" -> attr.isStatic)
    }
  }

  implicit object DeviceAttributeReader extends BSONDocumentReader[DeviceAttribute] {
    def read(doc: BSONDocument): DeviceAttribute = {
      DeviceAttribute(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("title").get.value,
        doc.getAs[String]("value"),
        doc.getAs[String]("measure"),
        doc.getAs[Int]("isStatic"))
    }
  }
}

object DeviceAttributeJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val deviceAttributeJsonFormat = jsonFormat5(DeviceAttribute.apply)
}

/**
 * Device model
 */
case class Device(
  _id: Option[BSONObjectID],
  title: String,
  created: Option[DateTime],
  attributes: Option[List[DeviceAttribute]])

object Device {

  implicit object DeviceWriter extends BSONDocumentWriter[Device] {
    def write(device: Device): BSONDocument = {
      BSONDocument(
        "title" -> device.title,
        "created" -> device.created.map(_.getMillis()),
        "attributes" -> device.attributes)
    }
  }

  implicit object DeviceReader extends BSONDocumentReader[Device] {
    def read(doc: BSONDocument): Device = {
      Device(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONString]("title").get.value,
        doc.getAs[BSONLong]("created").map(_.value.toDateTime),
        doc.getAs[List[DeviceAttribute]]("attributes"))
    }
  }
}

object DeviceJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.DeviceAttributeJsonProtocol._
  import bp.trainapp.model.BaseModel._
  implicit val deviceJsonFormat = jsonFormat4(Device.apply)
}
