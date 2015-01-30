package bp.trainapp.repository

import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent.ExecutionContext.Implicits.global

import org.joda.time.DateTime

import reactivemongo.api._
import reactivemongo.bson._

import bp.trainapp.model._

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
	
	def createFrom(e: Entity) = e match {
		case pc: ProgramClass => {
			val program = Program(
				_id = None,
				title = pc.title,
				created = Some(DateTime.now()),
				userId = pc.userId,
				devices = pc.devices,
				isDefault = None)
			save(program)
		}
		case puc: ProgramUpdateClass => {
			val program = Program(
				_id = None,
				title = puc.title,
				created = None,
				userId = None,
				devices = puc.devices,
				isDefault = puc.isDefault)
			save(program)
		}
		case _ => throw new Exception("createFrom not implemented for " + e.toString())
	}

}