package example.futures

import example.Logger.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

object FuturesRecoveryDemo {
  def main(args: Array[String]): Unit = {
    println("started")

    val activity = Future {
      log("processing activity started")
      Thread.sleep(1000)
      log("processing activity failure simulation")

      throw new RuntimeException("processing failed")
    }

    val activityWithRecovery = activity.recoverWith {
      case ex: Exception => {
        log("processing activity failure detected: " + ex + " - recovering")
        // performRecovery()
        performFailedRecovery()
      }
    }

    activityWithRecovery.onComplete {
      case Success(_) => log("processing activity completed")
      case Failure(ex) => log("processing activity recovery failed: " + ex)
    }

    Await.ready(activityWithRecovery, Duration.Inf)
  }

  private def performRecovery(): Future[Unit] = {
    Future {
      log("recovery process started")
      Thread.sleep(100)
      log("recovery process completed")
    }
  }

  private def performFailedRecovery() = {
    Future {
      log("recovery process started")
      Thread.sleep(50)
      log("recovery process failure simulation")

      throw new RuntimeException("recovery failed")
    }
  }
}
