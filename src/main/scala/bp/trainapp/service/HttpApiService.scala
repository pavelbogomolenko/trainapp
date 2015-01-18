package bp.trainapp.service

import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor

import org.slf4j.LoggerFactory

import org.joda.time.DateTime

import spray.routing._
import spray.http._
import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.httpx.SprayJsonSupport
import spray.json._
import spray.routing.Directive.pimpApply

import reactivemongo.bson._

import bp.trainapp.repository._
import bp.trainapp.model._
import bp.trainapp.service._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class HttpApiServiceActor extends Actor with HttpApiService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// this trait defines our service behavior independently from the service actor
trait HttpApiService extends HttpService with SprayJsonSupport with RepositoryComponent {
  
  val API_ROUET_PREFIX = "api"
  val API_VERSION = "1.0"
    
  //logger
  val log = LoggerFactory.getLogger(classOf[HttpApiService])
	
  def getJson(route: Route): Route = {
    get {
      respondWithMediaType(`application/json`) {
        route
      }
    }
  }
  
//  def auth(route: Route): Route = {
//     headerValueByName("X-Auth") { sessionId =>
//       val res = authService.validateSession(sessionId)
//       onComplete(res) {
//         case Success(r:UserSession) => route
//         case Failure(e) => failWith(e)
//       }
//    }
//  }
  
//  val authService= new AuthComponent with AuthService {
//    val repComp = repositoryComponent
//    val sessionLifetime = SessionConfig.sessionLifetime.toLong
//  }
  
  /**
   * routes definition
   */
  val myRoute = 
  	path(API_ROUET_PREFIX / API_VERSION / "user") {
    	//auth {
	  	  getJson {
		      complete {
		      	import bp.trainapp.model.UserJsonProtocol._
		      	userRepository.list()
		      }
	  	  } ~
	  	  post {
	  	    import bp.trainapp.model.UserClassJsonProtocol._
	  	    entity(as[UserClass]) { (u) =>
	  	      respondWithMediaType(`application/json`) {
		  	      val res = userRepository.createFromUserClass(u)
		  	      onComplete(res) {
		  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
		  	        case Failure(e) => failWith(e)
		  	      } 
	  	      }
	  	    }
	  	  }
    	//}
  	}
//  	path(API_ROUET_PREFIX / API_VERSION / "login") {
//  	  import bp.trainapp.model.UserClassJsonProtocol._
//  	  //@to-do check if user with given session already logged-in
//  	  entity(as[UserClass]) { (u) =>
//  	    respondWithMediaType(`application/json`) {
//  	      val res = authService.login(u.email, u.password)
//  	      onComplete(res) {
//	  	    	case Success(r:UserSession) => {
//	  	    	  respondWithHeader(RawHeader("X-Auth", r.sessionId)) {
//	  	    	    import bp.trainapp.model.UserSessionJsonProtocol._
//	  	    	  	complete(StatusCodes.OK, r)
//	  	    	  }
//	  	    	} 
//	  	      case Failure(e) => complete(StatusCodes.Unauthorized)
//	  	    } 
//  	    }
//  	  }
//  	} ~
//  	path(API_ROUET_PREFIX / API_VERSION / "logout") {
//  	  auth {
//	  	  headerValueByName("X-Auth") { sessionId =>
//  	      val res = authService.logout(sessionId)
//  	      onComplete(res) {
//	  	    	case Success(r) => complete(StatusCodes.NoContent) 
//	  	      case Failure(e) => failWith(e)
//	  	    }
//	  	  }
//  	  }
//  	} ~
//  	path(API_ROUET_PREFIX / API_VERSION / "usersession") {
//    	getJson {
//    	  complete {
//    	  	import bp.trainapp.model.UserSessionJsonProtocol._
//    	    repositoryComponent.userSessionRepository.list[UserSession]()
//    	  }
//    	}
//  	} ~ 
//  	path(API_ROUET_PREFIX / API_VERSION / "device") {
//  	  auth {
//	  	  getJson {
//	    	  complete {
//	    	  	import bp.trainapp.model.DeviceJsonProtocol._
//	    	    repositoryComponent.deviceRepository.list[Device]()
//	    	  }
//	    	} ~
//	    	post {
//	    	  import bp.trainapp.model.DeviceClassJsonProtocol._
//	    		entity(as[DeviceClass]) { (device) =>
//	  	    	respondWithMediaType(`application/json`) {
//		  	      val res = repositoryComponent.deviceRepository.createFrom(device)
//		  	      onComplete(res) {
//		  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
//		  	        case Failure(e) => failWith(e)
//		  	      } 
//	  	    	}
//	  	    }
//	    	} ~
//	    	put {
//	    	  import bp.trainapp.model.DeviceUpdateClassJsonProtocol._
//	    		entity(as[DeviceUpdateClass]) { (deviceUpdate) =>
//	  	    	respondWithMediaType(`application/json`) {
//		  	      val res = repositoryComponent.deviceRepository.createFrom(deviceUpdate)
//		  	      onComplete(res) {
//		  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
//		  	        case Failure(e) => failWith(e)
//		  	      } 
//	  	    	}
//	  	    }
//	    	}
//  	  }
//  	}
}