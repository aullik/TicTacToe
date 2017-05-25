package tictactoe.persistence

import tictactoe.persistence.databaseAccess.cache.CachePersistence
import tictactoe.persistence.databaseAccess.db4o.DB4OPersistence
import tictactoe.persistence.databaseAccess.file.FilePersistence
import tictactoe.persistence.databaseAccess.mongo.MongoPersistence
import tictactoe.persistence.databaseAccess.rest.RestPersistence
import tictactoe.persistence.databaseAccess.slick.SlickPersistence

/**
  */
sealed trait PersistenceEnvironment {
  val persistence: Persistence
}


object TestPersistenceEnvironment extends PersistenceEnvironment {
  override val persistence = new CachePersistence
}

object DevelopmentPersistenceEnvironment extends PersistenceEnvironment {
  override val persistence = new FilePersistence("persistence")
}

object ProductivePersistenceEnvironmentMongo extends PersistenceEnvironment {
  override val persistence = new MongoPersistence("localhost", 27017, "tictactoe")
}

object ProductivePersistenceEnvironmentSlick extends PersistenceEnvironment {
  override val persistence = new SlickPersistence()
}

object ProductivePersistenceEnvironmentDB4O extends PersistenceEnvironment {
  override val persistence = new DB4OPersistence()
}/*
object ProductivePersistenceEnvironmentRESTAPI extends PersistenceEnvironment {
  override val persistence = new RestPersistence("http://restapi.dev")
}*/
