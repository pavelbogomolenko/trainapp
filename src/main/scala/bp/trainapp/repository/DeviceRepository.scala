package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service.{MongoDbDriver, MongoDbDriverException}
import bp.trainapp.model.{Entity, Device, DeviceClass, DeviceUpdateClass}

class DeviceRepository[T](override val db:MongoDbDriver) extends BaseRepository(db) {    
  val collectionName = "trainapp.device"
    
  def save(device: Device) = device match {
    case Device(_id, _, _, _, _) if _id != None => {
      val selector = BSONDocument("_id" -> device._id.get)
      val modifier = BSONDocument(
      		"$set" -> BSONDocument(
      				"title" -> device.title,
      				"attributes" -> device.attributes,
      				"last_trained" -> device.last_trained.map(_.getMillis())))  
      				
      val res = db.collection(collectionName).update(selector, modifier) 
      res onComplete {
	      case Success(result)  => result
	      case Failure(failure) => throw new MongoDbDriverException("Failed to update Device " 
	          + failure.getMessage())
	    }
      res
    }
    case Device(None, title, created, attributes, last_trained) => {
    	val res = db.collection(collectionName).insert(device)
    	res onComplete {
	      case Success(result)  => result
	      case Failure(failure) => throw new MongoDbDriverException("Failed to insert Device " 
	          + failure.getMessage())
	    }
    	res
    }
  }
    
  def createFrom(d: Entity) = d match {
    case dc:DeviceClass => {
	  	val device = Device(
	  	    _id = None,
	  	    title = dc.title,
	  	    created = DateTime.now(),
	  	    attributes = dc.attributes,
	  	    last_trained = None)
	    save(device)
    }
    case duc:DeviceUpdateClass => throw new Exception("implement createFrom DeviceUpdateClass")
  }
}