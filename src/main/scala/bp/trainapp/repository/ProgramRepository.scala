package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.model.Program

class ProgramRepository extends BaseRepository {

	type Model = Program

	val collectionName = "program"

	def save(program: Program) = program match {
		case Program(_id, _, _, _, _, _) if _id != None => {
			val selector = BSONDocument("_id" -> program._id.get)
			val modifier = BSONDocument(
				"$set" -> BSONDocument(
					"title" -> program.title,
					"devices" -> program.devices,
					"isDefault" -> program.isDefault))
			update(selector, modifier)
		}
		case Program(None, title, created, userId, devices, isDefault) => insert(program)
	}

	def list(): Future[List[Model]] = {
		super.list[Model]()
	}

	def findByUserId(userId: BSONObjectID) = {
		val query = BSONDocument("userId" -> userId)
		super.list[Model](query)
	}

}