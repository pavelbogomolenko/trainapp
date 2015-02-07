package bp.trainapp.model

import scala.util.Try

import spray.json._

import reactivemongo.bson._

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

import org.apache.commons.codec.binary.Hex

object BaseModel {
  /**
   * implicit marshaling from and to BSONObjectID
   */
  implicit object BSONObjectIDFormat extends RootJsonFormat[BSONObjectID] {
    def write(objectId: BSONObjectID): JsValue = JsString(Hex.encodeHexString(BSONObjectID.unapply(objectId).get))
    def read(json: JsValue) = json match {
      case JsString(x) => {
        val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(x)
        if (maybeOID.isSuccess) maybeOID.get
        else {
          throw new DeserializationException("Expected BSONObjectID as JsString")
        }
      }
      case _ => throw new DeserializationException("Expected BSONObjectID as JsString")
    }
  }
  /**
   * implicit marshaling from and to DateTime
   */
  implicit object DateTimeFormat extends RootJsonFormat[DateTime] {
    def write(dt: DateTime): JsValue = JsString(dt.toString())
    def read(json: JsValue) = json match {
      case JsNumber(dt) => dt.toLong.toDateTime
      case _            => throw new DeserializationException("Expected DateTime as JsString")
    }
  }
}