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
  devices: Option[List[Device]],
  start: Option[DateTime],
  finish: Option[DateTime])

object Training {

  implicit object TrainingWriter extends BSONDocumentWriter[Training] {
    def write(training: Training): BSONDocument = {
      BSONDocument(
        "userId" -> training.userId,
        "programId" -> training.programId,
        "devices" -> training.devices,
        "start" -> training.start.map(_.getMillis()),
        "finish" -> training.finish.map(_.getMillis()))
    }
  }

  implicit object ProgrammReader extends BSONDocumentReader[Training] {
    def read(doc: BSONDocument): Training = {
      Training(
        doc.getAs[BSONObjectID]("_id"),
        doc.getAs[BSONObjectID]("userId"),
        doc.getAs[BSONObjectID]("programId"),
        doc.getAs[List[Device]]("devices"),
        doc.getAs[BSONLong]("start").map(_.value.toDateTime),
        doc.getAs[BSONLong]("finish").map(_.value.toDateTime))
    }
  }
}

object TrainingJsonProtocol extends DefaultJsonProtocol {
  import bp.trainapp.model.BaseModel._
  import bp.trainapp.model.DeviceJsonProtocol._
  implicit val trainingJsonFormat = jsonFormat6(Training.apply)
}