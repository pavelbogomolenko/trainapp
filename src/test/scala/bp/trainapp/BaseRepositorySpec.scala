package bp.trainapp

import reactivemongo.core.commands._

class BaseRepositorySpec extends MongoDbTestBase {
	"BeforeExample" should {
	  "userRepository must be empty" in {
	    val futureUserStats = userRepository.stats
	    futureUserStats map {
	      case result:CollStatsResult => result.count 
	    } must be_== (0).await
	  }
	  
	  "userSessionRepository must be empty" in {
	   val futureUserSessionStats = userSessionRepository.stats
	    futureUserSessionStats map {
	      case result:CollStatsResult => result.count 
	    } must be_== (0).await
	  }
	  
	  "deviceRepository must be empty" in {	    
	    val futureDeviceStats = deviceRepository.stats
	    futureDeviceStats map {
	      case result:CollStatsResult => result.count 
	    } must be_== (0).await
	  }
	}
}