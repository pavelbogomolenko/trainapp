package bp.trainapp.utils

import scala.util.{ Success, Failure }
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import shapeless._

import spray.routing._
import spray.routing.Directives._

import spray.http.HttpHeaders.RawHeader
import spray.http.MediaTypes._
import spray.http.StatusCodes

import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.model._
import bp.trainapp.service._

/**
 * Spray directive mixin that provides authorization and provides a UserSession object
 * instance to next inner route
 */
trait SprayAuthDirective extends HttpService with AuthService {

	def auth: Directive[UserSession :: HNil] = {
		headerValueByName("X-Auth").flatMap {
			case sessionId => {
				val futureSession = validateSession(sessionId)
				onComplete(futureSession).flatMap {
					case Success(r: UserSession) => provide(r)
					case Failure(e) => complete(StatusCodes.Unauthorized, e)
				}
			} 
		}
	}
}