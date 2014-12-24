package bp.trainapp.repository

import bp.trainapp.service.DbDriverComponent
import bp.trainapp.service.MongoDbDriver

trait RepositoryComponent {
	this: DbDriverComponent =>
	
	lazy val userProfileRepository = new UserProfileRepository(db)
	lazy val userRepository = new UserRepository(db)
	lazy val userSessionRepository = new UserSessionRepository(db)
}

abstract class BaseRepository(val db:MongoDbDriver)