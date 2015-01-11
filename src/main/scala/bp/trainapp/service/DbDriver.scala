package bp.trainapp.service

import reactivemongo.api._
import scala.concurrent.ExecutionContext.Implicits.global


class MongoDbDriver(val host: String, val dbName: String) {
	lazy val connect = {
	  // gets an instance of the driver
		// (creates an actor system)
		val driver = new MongoDriver
		val connection = driver.connection(List(host))
		
		// Gets a reference to the database "dbName"
		connection(dbName)
	}
	
	def collection(name: String) = {
		connect(name)
	}
}

trait DbDriverComponent {
  val db:MongoDbDriver
}

class MongoDbDriverException(message: String) extends Exception(message)