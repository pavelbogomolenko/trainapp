package bp.trainapp.model

import scala.util.Try

import spray.json._

import reactivemongo.bson._

object BaseModel {
	implicit object BSONObjectIDFormat extends RootJsonFormat[BSONObjectID] {
	  def write(objectId: BSONObjectID): JsValue = JsString(objectId.toString())
	  def read(json: JsValue) = json match {
	    case JsString(x) => {
	      val maybeOID: Try[BSONObjectID] = BSONObjectID.parse(x)
	      if(maybeOID.isSuccess) maybeOID.get 
	      else {
	        throw new DeserializationException("Expected BSONObjectID as JsString")
	      }
	    }
	    case _ => throw new DeserializationException("Expected BSONObjectID as JsString")
	  }
	}
}