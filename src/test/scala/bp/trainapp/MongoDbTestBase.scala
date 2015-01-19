package bp.trainapp

import org.specs2.mutable.{Before, Specification}
import bp.trainapp.service.MongoDbDriverComponent

trait MongoDbTestBase extends Specification with Before with MongoDbDriverComponent {
  
  def cleanDB() = {
    println(db.dbName, db.host)
  }
  
  def before = cleanDB()
}