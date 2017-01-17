package tictactoe.model.entity


import org.bson.types.ObjectId


/**
  */
sealed trait EntityId extends Product {
  val asString: String

}

sealed trait IDFactory[ID <: EntityId] {
  def apply(): ID = ConcreteId().asInstanceOf[ID]

  def ofStringID(id: String): ID = ConcreteId(id).asInstanceOf[ID]

}

sealed trait UserId extends EntityId

object UserId extends IDFactory[UserId]


sealed trait GameId extends EntityId

object GameId extends IDFactory[GameId]


private case class ConcreteId(asString: String = EntityId.generateId()) extends GameId with UserId {
  if (asString == null)
    throw new IllegalArgumentException("ID is null")

  override def toString: String = asString
}


object EntityId {
  private[entity] def generateId(): String = new ObjectId().toString

}
