package tictactoe.persistence.entityManagement.mutator

import tictactoe.model.entity.{Entity, EntityId}
import tictactoe.persistence.ReentrantLockWrapper
import tictactoe.persistence.entityManagement.cache.EntityWrapper

/**
  */
private[entityManagement] class CacheMutator[ID <: EntityId, T <: Entity[ID]] private(
                                                                                       private var entity: EntityWrapper[T],
                                                                                       private val writeToDatabase: T => Unit
                                                                                     ) extends Mutator[ID, T] {

  private val lock = new ReentrantLockWrapper()

  private var isClearedFromCache = false

  override def get: T = {
    lock.lock()
    try {
      entity.get
    } finally
      lock.unlock()
  }

  override def update(updateFunction: (T) => T): Unit = {
    lock.lock()
    try {
      val old = entity.get
      val updatedEntity = updateFunction(old)
      entity.set(updatedEntity)
    } finally
      lock.unlock()
  }

  override def lockAndGet(): T = {
    if (lock.isHeldByCurrentThread) {
      println("trying to lock already lock i already hold")
      throw new IllegalStateException("Thread already holds this lock")
    }
    lock.lock()
    entity.get
  }

  override def updateAndUnlock(updatedValue: T): Unit = {
    checkLock()
    //no need to check decisions cache here
    try {
      entity.set(updatedValue)
    } finally
      lock.unlock()
  }

  override def unlock(): Unit = {
    checkLock()
    try {
      entity.used()
    } finally
      lock.unlock()
  }


  private def checkLock() = {
    if (!lock.isHeldByCurrentThread) {
      println("trying to lock already lock i already hold")
      throw new IllegalStateException("Thread does not hold this lock")
    }
  }


  private[persistence] def isInCache(): Boolean = {
    lock.lock()
    try {
      // no harm in updating lastUsed once 'isClearedFromCache' is set
      entity.used()
      !isClearedFromCache
    }
    finally lock.unlock()
  }

  /**
    *
    * @param maxTimeSinceLastUse returns true if this has not been used in the defined time
    * @return true when this should be cleared
    */
  private[persistence] def maintainAndCheckClear(maxTimeSinceLastUse: Long): Boolean = {
    if (!lock.tryLock()) return false
    try {
      if (entity.changedSinceClear) {
        //get must be called only if there is a change. Will reset used time.
        val changed = entity.get
        entity.clearState()
        writeToDatabase(changed)
      }

      val now = System.currentTimeMillis()
      isClearedFromCache = entity.getLastUsed < now - maxTimeSinceLastUse

      isClearedFromCache
    } finally
      lock.unlock()
  }

}


object CacheMutator {

  def apply[ID <: EntityId, T <: Entity[ID]](entity: T, writeToDatabase: T => Unit): CacheMutator[ID, T] = {
    new CacheMutator[ID, T](EntityWrapper[T](entity), writeToDatabase)
  }


}