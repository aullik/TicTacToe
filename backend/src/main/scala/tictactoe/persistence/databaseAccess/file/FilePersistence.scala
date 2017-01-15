package tictactoe.persistence.databaseAccess.file

import java.io.File

import tictactoe.model.entity.EntityId
import tictactoe.persistence.databaseAccess.PersistenceAccessor
import tictactoe.persistence.{Persistence, ReadWriteLockWrapper}

class FilePersistence(path: String) extends Persistence {


  private val rwl = new ReadWriteLockWrapper()

  override protected def createPersistenceConnection[ID <: EntityId](name: String): PersistenceAccessor[ID] =
    new FileAccessor(rwl, new File(path + "/" + name))
}
