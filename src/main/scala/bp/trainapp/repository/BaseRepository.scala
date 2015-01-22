package bp.trainapp.repository

import scala.util.{Success, Failure}

import scala.concurrent.{Future, ExecutionContext}
import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.service._
import bp.trainapp.model._

/**
 * Base class for all MongoDB specific repositories
 */
abstract class BaseRepository extends MongoDbDriverComponent {
  type Model
  val collectionName: String
  
  def list[Model](query: db.Q = BSONDocument())(implicit reader:db.Reader[Model]): Future[List[Model]] = {    
  	db.list[Model](collectionName, query)
  }
  
  def insert[Model](model: Model)(implicit writer:db.Writer[Model]): Future[_] = {
  	db.insert[Model](collectionName, model)
  }
  
  def update(selector: db.Q, modifier: db.Q): Future[_] = {
    db.update(collectionName, selector, modifier)
  }
  
  /**
   * remove data from table by query
   */
  def remove(query: db.Q = BSONDocument()) = {
    db.remove(collectionName, query)
  }
  
  /**
   * get statistical information about collection
   */
  def stats = {
    db.stats(collectionName)
  }
}

trait RepositoryComponent {
  lazy val userRepository = new UserRepository
  lazy val userSessionRepository = new UserSessionRepository
  lazy val deviceRepository = new DeviceRepository
}