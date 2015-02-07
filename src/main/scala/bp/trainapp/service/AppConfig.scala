package bp.trainapp.service

import com.typesafe.config._

/**
 * AppConfig
 */
trait AppConfig {
  private val config = ConfigFactory.load()
  private lazy val root = config.getConfig("trainapp")

  object DbConfig {
    private val dbConfig = root.getConfig("db")

    lazy val host = dbConfig.getString("host")
    lazy val name = dbConfig.getString("name")
  }

  object SessionConfig {
    private val sessionConfig = root.getConfig("session")

    lazy val sessionLifetime = sessionConfig.getString("sessionLifetime")
  }
}