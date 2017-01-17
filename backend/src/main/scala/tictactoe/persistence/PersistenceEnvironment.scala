package tictactoe.persistence

import tictactoe.persistence.databaseAccess.cache.CachePersistence
import tictactoe.persistence.databaseAccess.file.FilePersistence
import tictactoe.persistence.databaseAccess.mongo.MongoPersistence

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

object ProductivePersistenceEnvironment extends PersistenceEnvironment {
  override val persistence = new MongoPersistence("localhost", 27017, "tictactoe")
}