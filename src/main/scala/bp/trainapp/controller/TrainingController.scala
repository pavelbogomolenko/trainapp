package bp.trainapp.controller

import scala.util.{ Success, Failure, Try }
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.bson._

import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes

import bp.trainapp.service._
import bp.trainapp.model._
import bp.trainapp.repository._

trait TrainingController extends BaseController with AuthService with RepositoryComponent {
  
  def getLastTrainingAction: Response = {
    auth { userSession =>
      parameters('programId) { (programId) =>
        complete {
          import bp.trainapp.model.TrainingJsonProtocol._
          trainingRepository.findLastUserTraining(userSession.userId, BSONObjectID(programId))
        } 
      }
    }
  }
  
  def createTrainingAction: Response = {
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
  }
  
  def updateTrainingAction: Response = {
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