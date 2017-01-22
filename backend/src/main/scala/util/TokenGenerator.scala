package util

import java.math.BigInteger
import java.util.Random

import com.mohiva.play.silhouette.api.crypto.Base64

/**
  */
class TokenGenerator {

  private val rnd = new Random(java.lang.Double.doubleToRawLongBits(Math.random()) ^ System.nanoTime())
  //can overflow
  private var counter: Long = rnd.nextLong()


  private def toArray(long: Long): Array[Byte] = {
    val array = Array.ofDim[Byte](8)
    val source = BigInteger.valueOf(long).toByteArray
    Array.copy(source, 0, array, 0, if (source.length > 8) 8 else source.length)
    array
  }

  /*
  rnd
  cnt
  usr
  rnd
  cnt
   */
  def generateToken(s: String): String = {
    counter += 1

    val rnd = toArray(this.rnd.nextLong())
    val cnt = toArray(counter)
    val usr = toArray(s.hashCode)

    val arr = Array.ofDim[Byte](20)

    def update(i: Int, a: Array[Byte]): Unit = {
      val n = i % 5
      val r = i / 5
      val idx =
        if (n < 2)
          r * 2
        else if (n == 2)
          r
        else
          r * 2 + 1
      arr.update(i, a(idx))
    }

    for (i <- 0 to 19) {
      i % 5 match {
        case 0 => update(i, rnd)
        case 1 => update(i, cnt)
        case 2 => update(i, usr)
        case 3 => update(i, rnd)
        case 4 => update(i, cnt)
      }
    }

    Base64.encode(arr)
  }

}
