package example.futures

import example.Logger.log

import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise

object FuturesDemo {
  def main(args: Array[String]): Unit = {
    val asyncActivity = Future {
      log("async activity started")

      Thread.sleep(3000)

      log("async activity completed")

      "1"
    }.map(s => {
      log("mapping function started")

      Thread.sleep(100)

      log("mapping function completed")

      s.toInt
    }).flatMap { i =>
      val activityCompletionPromise = Promise[Int]

      val summationActivityThread = new Thread(new Runnable {
        def run(): Unit = {
          log("performing summation")
          Thread.sleep(50)
          activityCompletionPromise.success(i + 2)
        }
      }, "summing thread")
      summationActivityThread.start()

      activityCompletionPromise.future
    }.map { sum =>
      log(s"sum = $sum")
    }

    log(s"async activity completed: ${asyncActivity.isCompleted}")

    for (v <- asyncActivity) {
      log("async activity completion callback called")

      Thread.sleep(200)

      log("async activity completion callback completed")
    }

    val asyncActivityResult = Await.result(asyncActivity, Duration.Inf)

    log(s"async activity completed: ${asyncActivity.isCompleted}")

    log(s"async activity result: ${asyncActivityResult}")
    log(s"async activity result: ${Await.result(asyncActivity, Duration.Inf)}")
  }
}
