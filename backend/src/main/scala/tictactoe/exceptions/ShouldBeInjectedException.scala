package tictactoe.exceptions

/**
  */
class ShouldBeInjectedException extends RuntimeException {

}

object ShouldBeInjectedException {

  def apply() = new ShouldBeInjectedException()
}
