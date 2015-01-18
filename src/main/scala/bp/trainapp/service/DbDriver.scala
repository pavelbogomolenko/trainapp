package bp.trainapp.service

import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.api.collections.default._
import reactivemongo.bson._

import bp.trainapp.service._

/**
 * MongoDbDriver
 */
class MongoDbDriver(val host: String, val dbName: String) {
  type Connection = DefaultDB
  type Reader[T] = BSONDocumentReader[T]
  type Writer[T] = BSONDocumentWriter[T]
  type Q = BSONDocument
  
	lazy val connect: Connection = {
	  // gets an instance of the driver
		// (creates an actor system)
		val driver = new MongoDriver
		val connection = driver.connection(List(host))
		
		// Gets a reference to the database "dbName"
		connection(dbName)
	}
	
	def collection(name: String): BSONCollection = {
		connect(name)
	}
	
	def list[T](table: String, query: Q = BSONDocument())(implicit reader:Reader[T]): Future[List[T]] = {
		collection(table).
			find(query).
			cursor[T].
			collect[List]()
  }
	
	def insert[T](table: String, model: T)(implicit writer:Writer[T]): Future[_] = {
    val res = collection(table).insert(model)
    res onComplete {
      case Success(result)  => result
      case Failure(failure) => throw new MongoDbDriverException("Failed to insert record " 
          + failure.getMessage())
    }
  	res
  }
	
  def update(table: String, selector: Q, modifier: Q): Future[_] = {
    val res = collection(table).update(selector, modifier) 
    res onComplete {
      case Success(result)  => result
      case Failure(failure) => throw new MongoDbDriverException("Failed to update record " 
          + failure.getMessage())
    }
    res
  }
}

trait MongoDbDriverComponent extends AppConfig {
	val db = new MongoDbDriver(DbConfig.host, DbConfig.name)
}

class MongoDbDriverException(message: String) extends Exception(message)