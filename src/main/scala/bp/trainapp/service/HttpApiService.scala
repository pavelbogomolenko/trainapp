package bp.trainapp.service

import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.httpx.SprayJsonSupport

import bp.trainapp.repository.UserRepository
import bp.trainapp.service.DbDriverComponent
import bp.trainapp.service.MongoDbDriver

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
	
  def getJson(route: Route): Route = {
    get {
      respondWithMediaType(`application/json`) {
        route
      }
    }
  }
  
  
  val userRepository = new UserRepository with DbDriverComponent {
    val db = new MongoDbDriver("localhost", "trainapp")
  }
  
  /**
   * routes definition
   */
  val myRoute =
    path(API_ROUET_PREFIX / API_VERSION / "user") {
    	getJson {
    	  complete {
    	  	//"""{"user": "123"}"""
		    	userRepository.list().map(list => list.map(u => u.age))
    	  }
    	}
  	}
}