package util

/**
  */
object FunctionalHelper {
  def ofTuple[A, B, R](fnc: (A, B) => R): (((A, B)) => R) = t => fnc(t._1, t._2)

  def copy[V](v: V)(block: V => V): V = block(v)

}



