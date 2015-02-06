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

trait ProgramRoute extends HttpService 
	with SprayJsonSupport with SprayAuthDirective with RepositoryComponent {

	val programRoute =
		pathPrefix("program") {
			get {
        auth { userSession =>
  				complete {
  					import bp.trainapp.model.ProgramJsonProtocol._
  					programRepository.findByUserId(userSession.userId)
  				}
        }
			} ~
			post {
        auth { userSession =>
  				import bp.trainapp.model.ProgramClassJsonProtocol._
  				entity(as[ProgramClass]) { program =>
  					val res = programRepository.createFrom(program)
  					onComplete(res) {
  						case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
  						case Failure(e) => failWith(e)
  					}
  				}
        }
			} ~
			put {
        auth { userSession =>
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