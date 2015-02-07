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

  /**
   * @to-do add check if user has been already logged in and there is an existing session
   */
  def loginByEmail(login: String): Future[UserSession] = {
    val result = userRepository.findOneByLogin(login)
    result map {
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