package bp.trainapp

import org.specs2.mutable.{ Specification }
import org.specs2.specification.BeforeExample
import bp.trainapp.repository.RepositoryComponent

trait MongoDbTestBase extends Specification with BeforeExample with RepositoryComponent {

  def before = cleanDB

  /**
   * clear db collections
   */
  def cleanDB = {
    userRepository.remove()
    userSessionRepository.remove()
    deviceRepository.remove()
  }
}
