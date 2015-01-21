package bp.trainapp

import org.specs2.mutable.{Before, Specification}
import bp.trainapp.service.MongoDbDriverComponent

trait MongoDbTestBase extends Specification with Before with MongoDbDriverComponent {
  
  def cleanDB = {
    //db.drop("trainapp.device")
    println(db.dbName)
    db.remove(db.dbName + "." + "user")
    println("after remove")
    //db.drop("trainapp.usersession")
  }
  
  def before = cleanDB
}