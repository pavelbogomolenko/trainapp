package bp.trainapp.route

import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import spray.routing._
import spray.http._
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.httpx.SprayJsonSupport
import spray.json._

import reactivemongo.bson._

import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.model._
import bp.trainapp.service._


trait DeviceRoute extends HttpService 
	with SprayJsonSupport with AuthRoute with RepositoryComponent {
  
  val deviceRoute =
    pathPrefix("device") {
    	auth {
	  	  get {
	    	  complete {
	    	  	import bp.trainapp.model.DeviceJsonProtocol._
	    	    deviceRepository.list()
	    	  }
	    	} ~
	    	post {
	    	  import bp.trainapp.model.DeviceClassJsonProtocol._
	    		entity(as[DeviceClass]) { device =>
	  	      val res = deviceRepository.createFrom(device)
	  	      onComplete(res) {
	  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
	  	        case Failure(e) => failWith(e)
	  	      }
	  	    }
	    	} ~
	    	put {
	    	  import bp.trainapp.model.DeviceUpdateClassJsonProtocol._
	    		entity(as[DeviceUpdateClass]) { deviceUpdate =>
	  	      val res = deviceRepository.createFrom(deviceUpdate)
	  	      onComplete(res) {
	  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
	  	        case Failure(e) => failWith(e)
	  	      }
	  	    }
	    	}
  	  }
  	}
}