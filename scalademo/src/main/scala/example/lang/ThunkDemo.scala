package example.lang

import scala.collection.concurrent
import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

object ThunkDemo {
  private val results: concurrent.Map[String, Thunk[Int]] = new ConcurrentHashMap[String, Thunk[Int]].asScala

  private val key: String = "key"

  def main(args: Array[String]): Unit = {
    println("started")

    val thunk = Thunk {
      println("thunk computing")

      1
    }

    val storedThunk = results.putIfAbsent(key, thunk)

    val result = storedThunk.map(_.get).getOrElse(thunk.get)

    val anotherThunk = Thunk {
      println("another thunk computing")

      1
    }

    val storedThunk2 = results.putIfAbsent(key, anotherThunk)

    val result2 = storedThunk2.map(_.get).getOrElse(anotherThunk.get)

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
