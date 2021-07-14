package example.lang

import scala.util.Try

object TryDemo {
  def main(args: Array[String]): Unit = {
    Try {
      println("computing")

      1
    }

    println("completed")
  }
}
