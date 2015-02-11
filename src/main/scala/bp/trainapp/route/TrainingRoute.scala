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

trait TrainingRoute extends HttpService
  with SprayJsonSupport with SprayAuthDirective with RepositoryComponent {

  val trainingRoute =
    pathPrefix("training") {
      get {
        auth { userSession =>
          parameters('programId) { (programId) =>
            complete {
              import bp.trainapp.model.TrainingJsonProtocol._
              trainingRepository.findLastUserTraining(userSession.userId, BSONObjectID(programId))
            } 
          }
        }
      } ~
      post {
        auth { userSession =>
          import bp.trainapp.model.TrainingClassJsonProtocol._
          entity(as[TrainingClass]) { training =>
            val res = trainingRepository.createFrom(training, Some(userSession.userId))
            onComplete(res) {
              case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
              case Failure(e) => failWith(e)
            }
          }
        }
      } ~
      put {
        auth { userSession =>
          import bp.trainapp.model.TrainingUpdateClassJsonProtocol._
          entity(as[TrainingUpdateClass]) { training =>
            val res = trainingRepository.createFrom(training, Some(userSession.userId))
            onComplete(res) {
              case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
              case Failure(e) => failWith(e)
            }
          }
        }
      }
    }
}