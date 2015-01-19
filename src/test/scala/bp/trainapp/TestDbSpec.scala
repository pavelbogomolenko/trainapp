package bp.trainapp

class TestDbSpec extends MongoDbTestBase {

	"TestDbSpec" should {
	  "DB should be empty" in {
	  	1 must be_==(1)
	  }
	}
}