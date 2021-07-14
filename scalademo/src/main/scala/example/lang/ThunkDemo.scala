package example.lang

object ThunkDemo {
  def main(args: Array[String]): Unit = {
    println("started")

    val thunk = Thunk {
      println("computed")

      1
    }

    println("thunk value: " + thunk.get)

    println("completed")
  }
}

class Thunk[+A] private (private val value: () => A) {
  def get: A = value()
}

object Thunk {
  def apply[A](value: => A): Thunk[A] = {
    lazy val v = value
    new Thunk(() => v)
  }
}
