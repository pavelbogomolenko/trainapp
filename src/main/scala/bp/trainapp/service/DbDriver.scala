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

  /**
   * mongodb collection object
   */
  def collection(name: String): BSONCollection = {
    connect(name)
  }

  /**
   * helper function to resolve future response from reactivemongo
   */
  private def resolveFuture(f: Future[_], errMsg: String): Future[_] = {
    f onComplete {
      case Success(result)  => result
      case Failure(failure) => throw new MongoDbDriverException(errMsg + " " + failure.getMessage())
    }
    f
  }

  /**
   * wrapper for find method which return list of documents
   */
  def list[T](table: String, 
      query: Q = BSONDocument(), sort: Q = BSONDocument(), limit: Int = 0)(implicit reader: Reader[T]): Future[List[T]] = {
    collection(table).
      find(query).
      sort(sort).
      cursor[T].
      collect[List](limit)
  }
  
  /**
   * wrapper for find method which return only one document
   */
  def one[T](table: String, 
      query: Q = BSONDocument(), sort: Q = BSONDocument())(implicit reader: Reader[T]): Future[Option[T]] = {
    collection(table).
      find(query).
      sort(sort).
      one[T]
  }

  def insert[T](table: String, model: T)(implicit writer: Writer[T]): Future[_] = {
    val res = collection(table).insert(model)
    resolveFuture(res, "Failed to insert record")
  }

  def update(table: String, selector: Q, modifier: Q): Future[_] = {
    val res = collection(table).update(selector, modifier)
    resolveFuture(res, "Failed to update record")
  }

  /**
   * drop collection
   */
  def drop(table: String): Future[_] = {
    val res = collection(table).drop()
    resolveFuture(res, "Failed to drop table")
  }

  /**
   * remove data from table by query
   */
  def remove(table: String, query: Q = BSONDocument()) = {
    val res = collection(table).remove(query)
    resolveFuture(res, "Failed to remove data from table")
  }

  def stats(table: String) = {
    resolveFuture(collection(table).stats, "Failed to get stats")
  }
}

trait MongoDbDriverComponent extends AppConfig {
  val db = new MongoDbDriver(DbConfig.host, DbConfig.name)
}

class MongoDbDriverException(message: String) extends Exception(message)