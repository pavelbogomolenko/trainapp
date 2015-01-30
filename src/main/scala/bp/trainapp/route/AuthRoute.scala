package bp.trainapp.route

import scala.util.{ Success, Failure }
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
import bp.trainapp.utils.SprayAuthDirective
import bp.trainapp.model._
import bp.trainapp.service._

trait AuthRoute extends HttpService 
	with SprayJsonSupport with AuthService with SprayAuthDirective {

	val authRoute =
		pathPrefix("login") {
			import bp.trainapp.model.UserClassJsonProtocol._
			//@to-do check if user with given session already logged-in
			entity(as[UserClass]) { u =>
				val res = login(u.email, u.password)
				onComplete(res) {
					case Success(r: UserSession) => {
						respondWithHeader(RawHeader("X-Auth", r.sessionId)) {
							import bp.trainapp.model.UserSessionJsonProtocol._
							complete(StatusCodes.OK, r)
						}
					}
					case Failure(e) => complete(StatusCodes.Unauthorized)
				}
			}
		} ~
		pathPrefix("logout") {
			auth { userSession =>
				val res = logout(userSession.sessionId)
				onComplete(res) {
					case Success(r) => complete(StatusCodes.NoContent)
					case Failure(e) => failWith(e)
				}
			}
		}
}