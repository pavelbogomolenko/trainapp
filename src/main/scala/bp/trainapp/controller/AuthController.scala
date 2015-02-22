package bp.trainapp.controller

import scala.util.{ Success, Failure }
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

import spray.http.HttpHeaders.RawHeader
import spray.http.StatusCodes

import bp.trainapp.service._
import bp.trainapp.model._
import bp.trainapp.repository._

trait AuthController extends BaseController with AuthService with RepositoryComponent {
  
  def respondWithAuthHeader(futureSession: Future[UserSession]): Response = {
    onComplete(futureSession) {
      case Success(r: UserSession) => {
        respondWithHeader(RawHeader("X-Auth", r.sessionId)) {
          import bp.trainapp.model.UserSessionJsonProtocol._
          complete(StatusCodes.OK, r)
        }
      }
      case Failure(e) => complete(StatusCodes.Unauthorized, """{"status": "auth fail"}""")
    }
  }
  
  def isLoginAction: Response = {
     auth { userSession =>
      import bp.trainapp.model.UserSessionJsonProtocol._
      complete(userSession)
    }
  }
  
  def loginAction: Response = {
    import bp.trainapp.model.UserClassJsonProtocol._
    //@to-do check if user with given session already logged-in
    entity(as[UserClass]) { u =>
      val futureSession = login(u.email, u.password.getOrElse(""))
      respondWithAuthHeader(futureSession)
    }
  }
    
  def fbLoginAction: Response = {
    import bp.trainapp.model.UserClassJsonProtocol._
    //@to-do check if user with given session already logged-in
    entity(as[UserClass]) { u =>
      optionalHeaderValueByName("X-Auth") {
        case None => {
          val findUserFuture = userRepository.findOneByLogin(u.email)
          onComplete(findUserFuture) {
            case Success(success0) => {
              success0 match {
                //if user found in DB
                case user:User => {
                  val futureSession = loginByEmail(user.email)
                  respondWithAuthHeader(futureSession)
                }
                //if user not found in DB
                case _ => {
                  println("trying to create temp user...")
                  val resp = userRepository.createFrom(u)
                  onComplete(resp) {
                    case Success(success1) => {
                      println("loginByEmail newUser: User")
                      val futureSession = loginByEmail(u.email)
                      respondWithAuthHeader(futureSession)
                    }
                    //weird exception
                    case Failure(e) => {
                      println("ERROR weird exception!")
                      failWith(e)
                    }
                  }
                }
              }
            }
            //weird exception(DB)
            case Failure(e) => {
              println("ERROR weird exception 2!")
              failWith(e)
            }
          }
        }
        case xAuth => {
          val futureSession = loginBySessionId(xAuth.getOrElse(""))
          respondWithAuthHeader(futureSession)
        }
      }
    }
  }
  
  def logoutAction: Response = {
    auth { userSession =>
      val res = logout(userSession.sessionId)
      onComplete(res) {
        case Success(r) => complete(StatusCodes.NoContent)
        case Failure(e) => failWith(e)
      }
    }
  }
}