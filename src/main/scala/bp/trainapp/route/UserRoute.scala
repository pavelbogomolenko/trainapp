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
import bp.trainapp.model._
import bp.trainapp.service._
import bp.trainapp.utils.SprayAuthDirective

trait UserRoute extends HttpService with SprayJsonSupport with 
	SprayAuthDirective with RepositoryComponent {

	val userRoute =
		pathPrefix("user") {
			auth { userSession =>
				get {
					complete {
						import bp.trainapp.model.UserJsonProtocol._
						userRepository.list()
					}
				} ~
				post {
					import bp.trainapp.model.UserClassJsonProtocol._
					entity(as[UserClass]) { (u) =>
						val res = userRepository.createFromUserClass(u)
						onComplete(res) {
							case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
							case Failure(e) => failWith(e)
						}
					}
				}
			}
		}
}