package bp.trainapp.service

import scala.util.{Success, Failure}
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor

import org.slf4j.LoggerFactory

import org.joda.time.DateTime

import spray.routing._
import spray.http._
import spray.json._
import spray.http.MediaTypes._
import spray.http.StatusCodes
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.httpx.SprayJsonSupport

import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.repository.UserExistsException

import bp.trainapp.model.User
import bp.trainapp.model.UserSession

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
trait HttpApiService extends HttpService with SprayJsonSupport {
  
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
  
  val repositoryComponent = new RepositoryComponent with DbDriverComponent {
    val db = new MongoDbDriver("localhost", "trainapp")
  }
  val authService= new AuthComponent with AuthService {
    val repComp = repositoryComponent
  }
  
  /**
   * routes definition
   */
  val myRoute = 
    path(API_ROUET_PREFIX / API_VERSION / "userprofile") {
    	getJson {
    	  complete {
    	    import bp.trainapp.model.UserProfileJsonProtocol._
    	    repositoryComponent.userProfileRepository.list()
    	  }
    	}
  	} ~
  	path(API_ROUET_PREFIX / API_VERSION / "user") {
  	  getJson {
  	    complete {
  	      import bp.trainapp.model.UserJsonProtocol._
  	      repositoryComponent.userRepository.list()
  	    }
  	  } ~
  	  post {
  	    import bp.trainapp.model.UserJsonProtocol._
  	    formFields('email, 'password) { (email, password) =>
  	      respondWithMediaType(`application/json`) {
	  	      val user = User(_id = None, 
	  	          email = email, password = password, created = DateTime.now().toString())
	  	      val res = repositoryComponent.userRepository.save(user)
	  	      onComplete(res) {
	  	        case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""") 
	  	        case Failure(e) => failWith(e)
	  	      } 
  	      }
  	    }
  	  }
  	} ~
  	path(API_ROUET_PREFIX / API_VERSION / "login") {
  	  formFields('email, 'password) { (email, password) =>
  	    respondWithMediaType(`application/json`) {
  	      val res = authService.auth(email, password)
  	      onComplete(res) {
	  	    	case Success(r) => {
	  	    	  println(r)
	  	    	  complete(StatusCodes.OK, "Success test")
	  	    	} 
	  	      case Failure(e) => complete(StatusCodes.Unauthorized)
	  	    } 
  	    }
  	  }
  	} ~
  	path(API_ROUET_PREFIX / API_VERSION / "usersession") {
    	getJson {
    	  complete {
    	  	import bp.trainapp.model.UserSessionJsonProtocol._
    	    repositoryComponent.userSessionRepository.list()
    	  }
    	}
  	}
}