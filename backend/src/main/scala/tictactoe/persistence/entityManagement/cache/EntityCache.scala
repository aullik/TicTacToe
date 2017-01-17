package tictactoe.persistence.entityManagement.cache

import java.util.concurrent.TimeUnit._

import grizzled.slf4j.Logging
import tictactoe.model.entity.{Entity, EntityId}
import tictactoe.persistence.ReadWriteLockWrapper
import tictactoe.persistence.entityManagement.mutator.CacheMutator

import scala.collection.mutable


/**
  */
private[entityManagement] class EntityCache[ID <: EntityId, E <: Entity[ID]](persistEntity: E => Unit) extends Logging {


  val cacheDuration: Long = MINUTES.toMillis(10)
  val maintainIntervalTime: Long = SECONDS.toMillis(10)

  private val rwl = new ReadWriteLockWrapper()
  private val readLock = rwl.readLock
  private val writeLock = rwl.writeLock

  private var maintainFlag = false
  private var maintainCache: List[(ID, CacheMutator[ID, E])] = Nil

  private val cache = mutable.HashMap.empty[EntityId, CacheMutator[ID, E]]

  val maintainThread = new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        Thread.sleep(maintainIntervalTime)
        maintain()
      }
    }
  })
  maintainThread.setDaemon(true)
  maintainThread.start()


  /**
    * the new entity will be added to the database, even if old value exists
    *
    * @param entity to add to database
    * @return
    */
  def add(entity: E): CacheMutator[ID, E] = {
    get(entity.id) match {
      case Some(old) =>
        old.update(entity)
        old

      case None =>
        put(entity.id, _ => entity)
    }
  }


  /**
    * supplier will only be used if no old value can be found
    *
    * @param id       the id of the returned Mutator
    * @param supplier supplies a new value
    * @return
    */
  def getOrElseAdd(id: ID, supplier: () => E): CacheMutator[ID, E] = {
    get(id) match {
      case Some(mutator) =>
        mutator

      case None =>
        put(id, _.map(_.get).getOrElse(supplier()))
    }

  }


  def get(id: ID): Option[CacheMutator[ID, E]] = {
    readLock.lock()
    try {
      val ofCache =
        if (maintainFlag)
          maintainCache.find(_._1 == id).map(_._2)
        else
          None

      ofCache.orElse(
        cache.get(id)
          //do while locked to ensure used time gets reset
          .filter(_.isInCache())
      )
    } finally readLock.unlock()
  }


  /**
    * will only be called after [[get()]] return None
    *
    * @param id       of entity
    * @param supplier supplies mutator to be inserted
    * @return mutator for id in cache
    */
  private def put(id: ID, supplier: Option[CacheMutator[ID, E]] => E): CacheMutator[ID, E] = {
    writeLock.lock()
    try {
      val toAdd = CacheMutator[ID, E](supplier(cache.get(id)), persistEntity)

      if (maintainFlag)
        maintainCache = (id, toAdd) :: maintainCache
      else
        cache.update(id, toAdd)

      toAdd
    } finally writeLock.unlock()
  }


  private def maintain(): Unit = {
    trace("maintaining EntityCache")

    readLock.lock()
    val snapshot: List[(EntityId, CacheMutator[ID, E])] =
      try {
        cache.toList
      } finally readLock.unlock()
    maintainList(snapshot)
  }

  private def maintainList(list: List[(EntityId, CacheMutator[ID, E])]): Unit = {

    val maintain_iter: Iterator[(EntityId, CacheMutator[ID, E])] =
      list.filter(entity => entity._2.maintainAndCheckClear(cacheDuration)).iterator

    if (!maintain_iter.hasNext) return


    // releases lock from time to time to give others a chance to work
    while (maintain_iter.hasNext) {
      writeLock.lock()
      try {
        /*
        This flag can only be set while write-locked
        No i don't have to set this every time, but this is the fastest and cleanest approach I could think of.
         */
        maintainFlag = true
        while (maintain_iter.hasNext && !rwl.hasQueuedThreads)
          cache.remove(maintain_iter.next()._1)
      } finally writeLock.unlock()
    }

    writeLock.lock()
    try {
      maintainFlag = false
      maintainCache.foreach(t => cache.update(t._1, t._2))
      maintainCache = Nil
    } finally writeLock.unlock()


  }

}
