package bp.trainapp.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import spray.json._
import spray.json.DefaultJsonProtocol._
import spray.http.MediaTypes._
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply

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
trait HttpApiService extends HttpService {
	
  def getJson(route: Route): Route = {
    get {
      respondWithMediaType(`application/json`) {
        route
      }
    }
  }
	
  val myRoute =
    path("login") {
      post {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Login!</h1>
              </body>
            </html>
          }
        }
      }
    } ~
    path("json") {
      getJson {
          complete {
              """{"test": 123}"""
          }
      }
    }
}