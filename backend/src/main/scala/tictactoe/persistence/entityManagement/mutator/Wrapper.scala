package tictactoe.persistence.entityManagement.mutator


/**
  * Read-Only Wrapper for a Entity.
  */
trait Wrapper[T] {

  def get: T

}

