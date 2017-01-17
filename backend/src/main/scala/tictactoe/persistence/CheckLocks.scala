package tictactoe.persistence

import java.util.concurrent.locks.{Lock, ReentrantLock, ReentrantReadWriteLock}

import grizzled.slf4j.Logging
import util.FunctionalHelper.ofTuple

import scala.collection.mutable

/**
  */
object CheckLocks extends Logging {

  lazy val isDebug = isDebugEnabled

  private class ThreadLock extends ThreadLocal[mutable.Map[Lock, Int]] {
    override def initialValue(): mutable.Map[Lock, Int] = mutable.Map.empty
  }


  private val locks = new ThreadLock


  def lock(lck: Lock): Unit = lock(lck, () => {
    lck.lock()
    true
  })

  def tryLock(lck: ReentrantLock): Boolean = lock(lck, () => {
    lck.tryLock()
  })

  private def lock(lock: Lock, trylock: () => Boolean): Boolean = {
    if (isDebug) {
      val map = locks.get()
      val init: Int = map.getOrElse(lock, 0)
      if (init > 0) info("### lock: locking twice")
      val ret = trylock()
      if (ret)
        map.update(lock, init + 1)
      ret
    } else trylock()
  }

  def unlock(lock: Lock): Unit = {
    if (isDebug) {
      val map = locks.get()
      val old = map.getOrElse(lock, 0)
      if (old <= 0) warn("### trying to lock unlocked Lock")
      map.put(lock, old - 1)
    }
    lock.unlock()
  }


  def check(): Unit = {
    if (!isDebug)
      return

    val held = locks.get().filter(ofTuple((l, i) => i > 0))

    if (held.isEmpty)
      return
    warn("Display all Locks currently held:\n" +
      held.map(ofTuple((l, i) => s"\t lock:  $l: times held:\t$i")).mkString("\n")
    )

    held.foreach(ofTuple((l, i) => (1 to i).foreach(_ => l.unlock())))
  }

}

private[persistence] class ReadWriteLockWrapper(private val rwl: ReentrantReadWriteLock = new ReentrantReadWriteLock()) {

  private lazy val read = new LockWrapper(rwl.readLock())
  private lazy val write = new LockWrapper(rwl.writeLock())

  def readLock = read

  def writeLock = write

  def hasQueuedThreads = rwl.hasQueuedThreads

  def getReadLockCount = rwl.getReadLockCount

  def isWriteLocked = rwl.isWriteLocked

}

private[persistence] class LockWrapper(private val concreteLock: Lock) {

  def lock() =
    CheckLocks.lock(concreteLock)

  def unlock() =
    CheckLocks.unlock(concreteLock)

}

private[persistence] class ReentrantLockWrapper(private val concreteLock: ReentrantLock = new ReentrantLock()) extends LockWrapper(concreteLock) {


  def isHeldByCurrentThread =
    concreteLock.isHeldByCurrentThread

  def tryLock() = CheckLocks.tryLock(concreteLock)

}
