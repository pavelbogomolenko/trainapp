package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._
import reactivemongo.bson.{BSONReader, BSONWriter}

case class Program(
    _id: Option[BSONObjectID],
		title: String,
		created: DateTime,
		userId: Option[BSONObjectID],
		devices: Option[List[BSONObjectID]],
		isDefault: Option[Int])
		
object Program {
  
  implicit object ProgramWriter extends BSONDocumentWriter[Program] {
  	def write(program: Program): BSONDocument = {
  	  BSONDocument(
  	      "title"							-> BSONString(program.title),
  	      "created"						-> BSONLong(program.created.getMillis()),
  	      "userId"						-> program.userId,
  	      "devices"						-> BSONArray(program.devices),
  	      "isDefault"					-> program.isDefault)
  	}
  }
  
  implicit object ProgrammReader extends BSONDocumentReader[Program] {
    def read(doc: BSONDocument): Program = {
		  Program(
		      doc.getAs[BSONObjectID]("_id"),
		      doc.getAs[BSONString]("title").get.value,
		      doc.getAs[BSONLong]("created").get.value.toDateTime,
		      doc.getAs[BSONObjectID]("userId"),
		      doc.getAs[List[BSONObjectID]]("attributes"),
		      doc.getAs[BSONInteger]("isStatic").map(_.value))
    }
  }
}

object ProgramJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val programJsonFormat = jsonFormat6(Program.apply)
}