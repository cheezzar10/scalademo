package example.lang

import scala.collection.concurrent
import cats.Eval

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

object EvalDemo {
  private val results: concurrent.Map[String, Eval[Int]] = new ConcurrentHashMap[String, Eval[Int]].asScala

  def main(args: Array[String]): Unit = {
    val result: Eval[Int] = Eval.later {
      println("computing value")

      1
    }

    println("result: " + result.value)
    println("result: " + result.value)

    val result1 = computeResult("request")
    println("result1: " + result1)

    val result2 = computeResult("request")
    println("result2: " + result2)
  }

  private def computeResult(key: String): Int = {
    println("retrieving result for key: " + key)

    val result = Eval.later {
      println("computing result for key: " + key)

      key.hashCode
    }

    val cachedResult = results.putIfAbsent(key, result)

    cachedResult
      .map(_.value)
      .getOrElse(result.value)
  }
}
