package bp.trainapp.service

import java.security.MessageDigest

import java.util.UUID

import org.joda.time.DateTime

trait AuthService {
	def generateToken: String = {
	  val token = UUID.randomUUID().toString() + DateTime.now().getMillis()
	  val md = MessageDigest.getInstance("SHA-1")
	  md.digest(token.getBytes()).toString()
	}
	
	def auth(login: String, pasword: String) = {
	  
	}
}