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
    case Device(_id, _, _, _, _, _, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> device._id.get)
      val modifier = BSONDocument(
        "$set" -> BSONDocument(
          "title" -> device.title,
          "userId" -> device.userId,
          "isPrototype" -> device.isPrototype,
          "attributes" -> device.attributes,
          "programId" -> device.programId,
          "trainingId" -> device.trainingId,
          "lastTrained" -> device.lastTrained.map(_.getMillis())))

      update(selector, modifier)
    }
    case Device(None, title, created, userId, attributes, isPrototype, programId, trainingId, lastTrained) => {
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
        userId = userId,
        isPrototype = None,
        attributes = dc.attributes,
        programId = None,
        trainingId = None,
        lastTrained = None)
      save(device)
    }
    case duc: DeviceUpdateClass => {
      val device = Device(
        _id = Some(duc.id),
        title = duc.title,
        created = None,
        userId = if (duc.userId != None) duc.userId else userId,
        isPrototype = None,
        attributes = duc.attributes,
        programId = None,
        trainingId = None,
        lastTrained = None)
      save(device)
    }
    case _ => throw new Exception("createFrom not implemented for " + d.toString())
  }
}