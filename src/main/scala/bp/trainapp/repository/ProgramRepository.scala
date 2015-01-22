package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.model.Program

class ProgramRepository extends BaseRepository {
    
  type Model = Program
  
  val collectionName = "program"
  
  def list(): Future[List[Model]] = {
    super.list[Model]()
	}
 
}