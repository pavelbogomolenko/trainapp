package bp.trainapp.service

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.UUID
import org.apache.commons.codec.digest.DigestUtils
import org.joda.time.DateTime
import bp.trainapp.repository.RepositoryComponent
import bp.trainapp.repository.UserNotFoundException
import bp.trainapp.model.UserSession
import bp.trainapp.model.User
import bp.trainapp.service._
import reactivemongo.bson.BSONObjectID

trait AuthService extends RepositoryComponent with AppConfig {

  val sessionLifetime: Long = SessionConfig.sessionLifetime.toLong

  def generateToken: String = {
    val token = UUID.randomUUID().toString() + "-" + DateTime.now().getMillis()
    val sha1ArrayByte = DigestUtils.sha1(token)
    DigestUtils.sha1Hex(sha1ArrayByte)
  }

  /**
   * @to-do do not store password un-encrypted
   */
  def login(login: String, password: String): Future[UserSession] = {
    val result = userRepository.findByCredentials(login, password)
    result map {
      case Nil => throw new UserNotFoundException("user not found")
      case List(user) => {
        val userSession = UserSession(
          _id = None,
          userId = user._id.get,
          sessionId = generateToken,
          ip = None,
          updated = DateTime.now(),
          expired = None)

        userSessionRepository.insert(userSession)
        userSession
      }
    }
  }

  def loginByEmail(login: String): Future[UserSession] = {
    val result = userRepository.findOneByLogin(login)
    result map {
      //user already in db
      case user:User => {
        val userSession = UserSession(
          _id = None,
          userId = user._id.get,
          sessionId = generateToken,
          ip = None,
          updated = DateTime.now(),
          expired = None)

        userSessionRepository.insert(userSession)
        userSession
      }
    }
  }
  
  def loginBySessionId(sessionId: String): Future[UserSession] = {
    val result = userSessionRepository.findOneBySesseionId(sessionId)
    result map {
      case userSession:UserSession => userSession
      //case None => loginByEmail(sessionId).
    }
  }

  def validateSession(sessionId: String): Future[UserSession] = {
    val result = userSessionRepository.findValidSession(sessionId, sessionLifetime)
    result map {
      case Nil               => throw new UserNotFoundException("session not valid or expired")
      case List(userSession) => userSession
    }
  }

  def logout(sessionId: String) = {
    val userSession = validateSession(sessionId)
    userSessionRepository.markAsExpired(userSession)
  }
}