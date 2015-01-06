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

trait AuthService {
  val repComp: RepositoryComponent
    
	def generateToken: String = {
	  val token = UUID.randomUUID().toString() + "-" + DateTime.now().getMillis()
	  val sha1ArrayByte = DigestUtils.sha1(token)
	  DigestUtils.sha1Hex(sha1ArrayByte)
	}
	
	def login(login: String, password: String) = {
		val result = repComp.userRepository.findByCredentials[User](login, password)
		result map {
			case Nil => Future.failed[String](throw new UserNotFoundException("user not found"))
      case List(user) => {
        val userSession = UserSession(
            _id = None,
            userId = user._id.get,
            sessionId = generateToken,
            ip = None,
            updated = DateTime.now().toString(), 
            expired = None)
        repComp.userSessionRepository.save(userSession)
        userSession
      }
		}
	}
	
	def validateSession(sessionId: String) = {
  	val result = repComp.userSessionRepository.findBySesseionId[UserSession](sessionId)
  	result map {
  		case Nil => Future.failed[String](throw new UserNotFoundException("session not valid or expired"))
  		case List(userSession) => userSession
  	}
	}
}

trait AuthComponent {
	this: AuthService =>
}