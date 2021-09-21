package example.futures

import scala.util.Try
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.Await

import cats.instances.option._
import cats.instances.list._
import cats.syntax.traverse._ // for xs.sequence

object TraversableTry {
  def main(args: Array[String]): Unit = {
    val strings = IndexedSeq("1", "10", "8", "2")

    println("result: " + sequenceWithFuture(strings))
    println("result: " + sequenceWithCats(strings.toList))
    println("result: " + sequenceManually(strings))
  }

  private def sequenceWithFuture(strings: IndexedSeq[String]): Try[IndexedSeq[Int]] = {
    val numbers = strings.map(str => Try(str.toInt))

    val result = Await.ready(
      Future.sequence(numbers.map(Future.fromTry)),
      Duration.Inf)

    result.value.get
  }

  private def sequenceWithCats(strings: List[String]): Option[List[Int]] = {
    val numbers = strings.map(str => Try(str.toInt).toOption)

    numbers.sequence
  }

  private def sequenceManually(strings: IndexedSeq[String]): Try[IndexedSeq[Int]] = {
    val numbers = strings.map(str => Try(str.toInt))

    numbers.foldLeft(Try(IndexedSeq.empty[Int])) {
      case (finalResult, attempt) => finalResult.flatMap { finalRes =>
        attempt.map { result =>
          finalRes :+ result
        }
      }
    }
  }
}
