package example

import scala.concurrent.ExecutionContext.Implicits.global
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import java.util.function.{Function => JFn}
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

class PipelineLoadingException(val name: String) extends RuntimeException(s"pipeline '$name' is not ready")

object CachedFuturesDemo {
  private val cache = new ConcurrentHashMap[String, Future[String]]

  private val counters = new ConcurrentHashMap[String, AtomicLong]

  private val FailedAttemptsCount = 2

  private val CacheLoadingFn = new JFn[String, Future[String]] {
    override def apply(name: String): Future[String] = {
      Future {
        println("loading pipeline: " + name)

        val counter = counters.computeIfAbsent(name, new JFn[String, AtomicLong] {
          override def apply(key: String): AtomicLong = new AtomicLong(0)
        })

        println("pipeline " + name + " loading attempts count: " + counter.get)

        if (counter.getAndIncrement() < FailedAttemptsCount) {
          println("pipeline is not ready")

          throw new PipelineLoadingException(name)
        } else {
          "pipeline_" + name
        }
      }
    }
  }

  private def loadPipeline(name: String): Future[String] = {
    println("requested pipeline: " + name)

    val pipelineLoading = cache.computeIfAbsent(name, CacheLoadingFn)

    pipelineLoading.onComplete {
      case Failure(ex) => {
        println("pipeline loading process failed: " + ex)

        ex match {
          case plex: PipelineLoadingException => {
            println("removing failed pipeline loading attempt from cache")
            cache.remove(plex.name)
          }
        }
      }
    }

    pipelineLoading

    /*
    if (pipelineLoading.isCompleted) {
      println("pipeline loading completed")

      pipelineLoading.value.get match {
        case Success(p) => Future.successful(p)
        case Failure(_) => {
          cache.remove(name)

          loadPipeline(name)
        }
      }
    } else {
      pipelineLoading
    }
     */
  }

  def main(args: Array[String]): Unit = {
    val pipelineName = "gazprom:1"

    try {
      val loadedPipeline = Await.result(loadPipeline(pipelineName), Duration.Inf)
      println("loaded pipeline: " + loadedPipeline)
    } catch {
      case ex: Exception => {
        println("pipeline loading failed: " + ex)

        try {
          val reloadedPipeline = Await.result(loadPipeline(pipelineName), Duration.Inf)
          println("reloaded pipeline: " + reloadedPipeline)
        } catch {
          case ex: Exception => {
            println("pipeline reloading failed: " + ex)

            val rereloadedPipeline = Await.result(loadPipeline(pipelineName), Duration.Inf)
            println("re-reloaded pipeline: " + rereloadedPipeline)
          }
        }
      }
    }
  }
}
