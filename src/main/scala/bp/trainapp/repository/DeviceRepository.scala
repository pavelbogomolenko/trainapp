package bp.trainapp.repository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.MongoDbDriver
import bp.trainapp.model.Device
import bp.trainapp.model.DeviceClass

class DeviceRepository[T](override val db:MongoDbDriver) extends BaseRepository(db) {    
  val collectionName = "trainapp.device"
    
  def createFromDeviceClass(d: DeviceClass) = {
  	val device = Device(
  	    _id = None,
  	    title = d.title,
  	    created = DateTime.now(),
  	    attributes = d.attributes,
  	    last_trained = None)
    save(device)
  }
}