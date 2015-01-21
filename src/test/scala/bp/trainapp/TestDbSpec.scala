package bp.trainapp

import reactivemongo.core.commands._

class TestDbSpec extends MongoDbTestBase {

	"TestDbSpec" should {
	  "MongoDB collections should be empty" in {
	    cleanDB
	    val futureStats = db.stats(db.dbName + "." + "user")
	    println("before test")
	    futureStats map {
	      case result:CollStatsResult => result.count 
	    } must be_== (0).await
	  }
	}
}