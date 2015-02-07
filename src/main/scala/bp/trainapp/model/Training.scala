package bp.trainapp.model

import spray.json._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import reactivemongo.bson._
import reactivemongo.bson.{ BSONReader, BSONWriter }

case class Training(
  _id: Option[BSONObjectID],
  userId: Option[BSONObjectID],
  programId: Option[BSONObjectID],
  start: DateTime,
  finish: Option[DateTime])

object Training {

  implicit object TrainingWriter extends BSONDocumentWriter[Training] {
    def write(training: Training): BSONDocument = {
      BSONDocument(
        "userId" -> training.userId,
        "programId" -> training.programId,
        "start" -> BSONLong(training.start.getMillis()),
        "finish" -> training.finish.map(_.getMillis()))
    }
  }

  implicit object ProgrammReader extends BSONDocumentReader[Training] {
    def read(doc: BSONDocument): Training = {
      Training(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONObjectID]("userId"),
        doc.getAs[BSONObjectID]("programId"),
        doc.getAs[BSONLong]("start").get.value.toDateTime,
        doc.getAs[BSONLong]("finish").map(_.value.toDateTime))
    }
  }
}

object TrainingJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  implicit val trainingJsonFormat = jsonFormat5(Training.apply)
}