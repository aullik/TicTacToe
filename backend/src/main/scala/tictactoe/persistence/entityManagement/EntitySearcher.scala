package tictactoe.persistence.entityManagement

import tictactoe.persistence.databaseAccess.PersistenceAccessor
import PersistenceAccessor._
import tictactoe.model.entity.{EntityId, IDFactory}


/**
  */
class EntitySearcher[ID <: EntityId](idFactory: IDFactory[ID],
                                     connection: PersistenceAccessor[ID]
                                    ) {
  def findAll(): List[ID] = connection.findAllDocuments().map(doc => idFactory.ofStringID(doc.getString(idKey)))
}
