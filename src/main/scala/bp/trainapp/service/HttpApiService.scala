package bp.trainapp.service

import scala.util.{ Success, Failure }
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

import bp.trainapp.utils.CorsSupport
import bp.trainapp.route._

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
trait HttpApiService extends HttpService with SprayJsonSupport with CorsSupport
	with UserRoute with AuthRoute with UserSessionRoute with DeviceRoute {

	val API_ROUET_PREFIX = "api"
	val API_VERSION = "1.0"

	//logger
	val log = LoggerFactory.getLogger(classOf[HttpApiService])

	/**
	 * routes definition
	 */
	val myRoute =
		pathPrefix(API_ROUET_PREFIX) {
			pathPrefix(API_VERSION) {
				cors {
					respondWithMediaType(`application/json`) {
						userRoute ~ authRoute ~ userSessionRoute ~ deviceRoute
					}
				}
			}
		}
}