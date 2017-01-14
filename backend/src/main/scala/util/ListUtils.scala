package util

/**
  */
object ListUtils {

  def removeFirst[E](e: E, list: List[E]): List[E] = {
    def rek(list: List[E]): List[E] = list match {
      case Nil => Nil
      case head :: tail =>
        if (head == e)
          rek(tail)
        else
          head :: rek(tail)
    }
    rek(list)
  }

}
