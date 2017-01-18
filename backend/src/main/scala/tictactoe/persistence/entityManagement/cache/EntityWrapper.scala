package tictactoe.persistence.entityManagement.cache

/**
  */
trait EntityWrapper[E] {
  protected var entity: E
  protected var changed = false


  protected var lastUsed = System.currentTimeMillis()

  def used(): Unit = {
    lastUsed = System.currentTimeMillis()
  }

  def getLastUsed = lastUsed

  def get: E = {
    used()
    entity
  }

  def changedSinceClear: Boolean = changed

  def clearState(): Unit = {
    changed = false
  }

  def set(e: E): Unit = {
    used()
    if (entity == e)
      return

    changed = true
    entity = e
  }

  def compare(other: E) = other == entity

}

private class StandardEntityWrapper[E](
                                        protected var entity: E
                                      ) extends EntityWrapper[E]


object EntityWrapper {
  def apply[E](entity: E): EntityWrapper[E] = {
    new StandardEntityWrapper[E](entity)
  }
}
