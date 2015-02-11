package bp.trainapp.repository

import scala.util.{ Success, Failure }

import scala.concurrent.{ Future, ExecutionContext }
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service._
import bp.trainapp.model._

/**
 * Base class for all MongoDB specific repositories
 */
abstract class BaseRepository extends MongoDbDriverComponent {
  /**
   * concrete Type Class that will be used to transform BPSON to Model
   */
  type Model
  /**
   * mongodb collection name
   */
  val collectionName: String

  private def fullCollectionName = db.dbName + "." + collectionName

  def list[Model](query: db.Q = BSONDocument(), 
      sort: db.Q = BSONDocument(), limit: Int = 100)(implicit reader: db.Reader[Model]): Future[List[Model]] = {
    db.list[Model](fullCollectionName, query, sort, limit)
  }
  
  def one[Model](query: db.Q = BSONDocument(), 
      sort: db.Q = BSONDocument())(implicit reader: db.Reader[Model]): Future[Option[Model]] = {
    db.one[Model](fullCollectionName, query, sort)
  }

  def insert[Model](model: Model)(implicit writer: db.Writer[Model]): Future[_] = {
    db.insert[Model](fullCollectionName, model)
  }

  def update(selector: db.Q, modifier: db.Q): Future[_] = {
    db.update(fullCollectionName, selector, modifier)
  }

  /**
   * remove data from table by query
   */
  def remove(query: db.Q = BSONDocument()): Future[_] = {
    db.remove(fullCollectionName, query)
  }

  /**
   * get statistical information about collection
   */
  def stats: Future[_] = {
    db.stats(fullCollectionName)
  }

  /**
   * Find document by id
   */
  def findOneById(id: BSONObjectID)(implicit reader: db.Reader[Model]): Future[Option[Model]] = {
    val query = BSONDocument("_id" -> id)
    one[Model](query)
  }

  /**
   * Find documents by list of ids
   */
  def findByIds(id: List[BSONObjectID])(implicit reader: db.Reader[Model]): Future[List[Model]] = {
    val query = BSONDocument("_id" -> BSONDocument("$in" -> id))
    list[Model](query)
  }
}

/**
 * Trait containing all available repository instances
 */
trait RepositoryComponent {
  lazy val userRepository = new UserRepository
  lazy val userSessionRepository = new UserSessionRepository
  lazy val deviceRepository = new DeviceRepository
  lazy val programRepository = new ProgramRepository
  lazy val trainingRepository = new TrainingRepository
}