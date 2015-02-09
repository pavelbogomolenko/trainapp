package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.{ MongoDbDriver, MongoDbDriverException }
import bp.trainapp.model.{ Entity, Device, DeviceClass, DeviceUpdateClass }

class DeviceRepository extends BaseRepository {

  type Model = Device
  val collectionName = "device"

  def save(device: Device) = device match {
    case Device(_id, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> device._id.get)
      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          "title" -> device.title,
          "attributes" -> device.attributes))

      update(selector, modifier)
    }
    case Device(None, title, created, attributes) => {
      insert(device)
    }
  }

  def list(): Future[List[Model]] = {
    super.list[Model]()
  }

  def createFrom(d: Entity, userId: Option[BSONObjectID] = None) = d match {
    case dc: DeviceClass => {
      val device = Device(
        _id = None,
        title = dc.title,
        created = Some(DateTime.now()),
        attributes = dc.attributes)
      save(device)
    }
    case duc: DeviceUpdateClass => {
      val device = Device(
        _id = Some(duc.id),
        title = duc.title,
        created = None,
        attributes = duc.attributes)
      save(device)
    }
    case _ => throw new Exception("createFrom not implemented for " + d.toString())
  }
}