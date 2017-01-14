package tictactoe.persistence.entityManagement.mutator

import tictactoe.model.entity.{Entity, EntityId}
import tictactoe.persistence.ReentrantLockWrapper

/**
  */
private[entityManagement] class ProxyMutator[ID <: EntityId, T <: Entity[ID]]
(
  private val supplier: () => CacheMutator[ID, T],
  private val maxCacheTime: Long,
  @volatile private var instance: CacheMutator[ID, T]
) extends Mutator[ID, T] {

  private val lock = new ReentrantLockWrapper()
  @volatile private var lastUsed = System.currentTimeMillis()

  /**
    * [[lastUsed]] and [[instance]] are volatile. [[instance]] is only updated while locked.
    * If [[lastUsed]] is too old, acquire lock.
    * [[instance]] may have been changed while waiting for lock, thus check again
    * Its irrelevant if [[lastUsed]] is overridden with a few nanoseconds older value
    * If [[instance]] is still in cache, there is no need to load it again.
    *
    */
  private def getInstance: Mutator[ID, T] = {
    val now = System.currentTimeMillis()
    if (lastUsed < now - maxCacheTime) {
      lock.lock()
      try {
        //need to check twice due to volatile system.
        if (lastUsed < now - maxCacheTime && !instance.isInCache())
          instance = supplier()
        lastUsed = now
      } finally lock.unlock()
    }
    lastUsed = now
    instance
  }

  override def get: T = getInstance.get

  @throws[IllegalStateException]("if the lock is not held by this thread")
  override def unlock(): Unit = getInstance.unlock()

  override def update(updateFunction: (T) => T): Unit = getInstance.update(updateFunction)

  @throws[IllegalStateException]("if the lock is already held by this thread")
  override def lockAndGet: T = getInstance.lockAndGet

  @throws[IllegalStateException]("if the lock is not held by this thread")
  override def updateAndUnlock(updatedValue: T): Unit = getInstance.updateAndUnlock(updatedValue)
}
