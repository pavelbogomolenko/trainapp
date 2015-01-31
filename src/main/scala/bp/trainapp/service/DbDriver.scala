package bp.trainapp.service

import scala.util.{ Success, Failure }
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

	/**
	 * establish connection
	 */
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

	private def validateFuture(f: Future[_], errMsg: String): Future[_] = {
		f onComplete {
			case Success(result) => result
			case Failure(failure) => throw new MongoDbDriverException(errMsg + " " + failure.getMessage())
		}
		f
	}

	def list[T](table: String, query: Q = BSONDocument())(implicit reader: Reader[T]): Future[List[T]] = {
		collection(table).
			find(query).
			cursor[T].
			collect[List]()
	}

	def insert[T](table: String, model: T)(implicit writer: Writer[T]): Future[_] = {
		val res = collection(table).insert(model)
		validateFuture(res, "Failed to insert record")
	}

	def update(table: String, selector: Q, modifier: Q): Future[_] = {
		val res = collection(table).update(selector, modifier)
		validateFuture(res, "Failed to update record")
	}

	/**
	 * drop collection
	 */
	def drop(table: String): Future[_] = {
		val res = collection(table).drop()
		validateFuture(res, "Failed to drop table")
	}

	/**
	 * remove data from table by query
	 */
	def remove(table: String, query: Q = BSONDocument()) = {
		val res = collection(table).remove(query)
		validateFuture(res, "Failed to remove data from table")
	}

	def stats(table: String) = {
		validateFuture(collection(table).stats, "Failed to get stats")
	}
}

trait MongoDbDriverComponent extends AppConfig {
	val db = new MongoDbDriver(DbConfig.host, DbConfig.name)
}

class MongoDbDriverException(message: String) extends Exception(message)