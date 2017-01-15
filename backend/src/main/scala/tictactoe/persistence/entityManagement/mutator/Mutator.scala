package tictactoe.persistence.entityManagement.mutator

import tictactoe.model.entity.{Entity, EntityId}


/**
  */
trait Mutator[ID <: EntityId, T <: Entity[ID]] {

  protected lazy val wrapperInstance: Wrapper[T] = new Wrapper[T] {
    override def get: T = Mutator.this.get
  }


  def wrapper: Wrapper[T] = wrapperInstance

  def get: T

  def update(updateFunction: T => T): Unit

  def update(updatedValue: T): Unit = update(_ => updatedValue)


  @throws[IllegalStateException]("if the lock is already held by this thread")
  def lockAndGet: T

  @throws[IllegalStateException]("if the lock is not held by this thread")
  def updateAndUnlock(updatedValue: T): Unit

  @throws[IllegalStateException]("if the lock is not held by this thread")
  def unlock(): Unit
}
