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

trait ProgramController extends BaseController with AuthService with RepositoryComponent {
  
  def findAction: Response = {
    auth { userSession =>
      parameters('id.?) { (id) =>
        if(id.get.isEmpty()) {
          complete {
            import bp.trainapp.model.ProgramJsonProtocol._
            programRepository.findByUserId(userSession.userId)  
          }
        }
        else {
          if(BSONObjectID.parse(id.get).isFailure) {
            complete(StatusCodes.BadRequest,  """{"status": "Expected BSONObjectID as JsString"}""") 
          } else {
            complete {
              import bp.trainapp.model.ProgramJsonProtocol._
              programRepository.findByUserIdAndId(userSession.userId, BSONObjectID.apply(id.get))
            }
          }
        }
      }
    }
  }
  
  def createProgramAction: Response = {
    auth { userSession =>
      import bp.trainapp.model.ProgramClassJsonProtocol._
      entity(as[ProgramClass]) { program =>
        val res = programRepository.createFrom(program, Some(userSession.userId))
        onComplete(res) {
          case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
          case Failure(e) => failWith(e)
        }
      }
    }
  }
  
  def updateProgramAction: Response = {
    auth { userSession =>
      import bp.trainapp.model.ProgramUpdateClassJsonProtocol._
      entity(as[ProgramUpdateClass]) { programUpdate =>
        val res = programRepository.createFrom(programUpdate, Some(userSession.userId))
        onComplete(res) {
          case Success(r) => complete(StatusCodes.Created, """{"status": "ok"}""")
          case Failure(e) => failWith(e)
        }
      }
    }
  }
}