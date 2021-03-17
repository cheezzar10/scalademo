package example

import Logger.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, blocking}
import scala.util.{Failure, Success}

case class Pipeline(name: String)

case class StoredPipeline(pipeline: Pipeline, path: String)

sealed trait ProcessStatus

case object Running extends ProcessStatus

case object Runnable extends ProcessStatus

case class Process(id: Long, status: ProcessStatus)

object PipelinesLoadingDemo {
  def main(args: Array[String]): Unit = {
    // performPipelineStoring()

    val result = for {
      maybeRunningProcess <- checkProcess("/ML-2521")
      process <- maybeRunningProcess
        .map(Future.successful(_))
        .getOrElse(startPipelineProcessing("nash_dom_gazprom_2"))
    } yield process

    Await.ready(result, Duration.Inf)

    log("process scheduled")

    val scheduledProcess = result.value.get.get

    println(s"pipeline handling process: $scheduledProcess")
  }

  private def startPipelineProcessing(pipelineName: String): Future[Process] = {
    log(s"starting pipeline processing: $pipelineName")

    for {
      maybePipeline <- loadPipeline(pipelineName)
      pipeline <- maybePipeline
        .map(Future.successful(_))
        .getOrElse(Future.failed(new IllegalArgumentException(s"pipeline $pipelineName not found")))
      pid <- startProcess(pipeline)
    } yield Process(id = pid, status = Runnable)
  }

  private def startProcess(pipeline: Pipeline): Future[Long] = {
    Future {
      log(s"scheduling pipeline $pipeline handling process")

      Thread.sleep(100)

      log(s"pipeline $pipeline handling process scheduled")

      2
    }
  }

  private def checkProcess(outputPath: String): Future[Option[Process]] = {
    Future {
      log(s"checking status of the process with output directed to: $outputPath")

      if (outputPath == "/ML-2520") Some(Process(1, Running)) else None
    }
  }

  private def performPipelineStoring(): Unit = {
    val storePipelineActivity = for {
      loadedPipeline <- loadPipeline("some")
      storedPipeline <- Future {
        blocking {
          for (p <- loadedPipeline) storePipeline(p)
        }
      }
    } yield storedPipeline

    Await.ready(storePipelineActivity, Duration.Inf)

    val storePipelineResult = storePipelineActivity.value

    println(s"store pipeline activity result: $storePipelineResult")

    val result = storePipelineResult.get match {
      case Success(p) => "stored pipeline loaded successfully"
      case Failure(e) => "attempt to store pipeline failed: " + e
    }

    println(s"result: $result")

    val storedPipeline = storePipelineResult.get.get

    println(s"stored pipeline: $storedPipeline")

    // log(s"stored pipeline: ${Await.result(storePipelineActivity, Duration.Inf)}")

    // log(s"stored pipeline: ${Await.result(storePipelineActivity, Duration.Inf)}")
  }

  private def loadPipeline(name: String): Future[Option[Pipeline]] = {
    Future {
      log(s"loading pipeline: $name")

      Thread.sleep(1000)

      if (name == "some") {
        throw new IllegalArgumentException("pipeline loading failed")
      }

      if (name == "nash_dom_gazprom") {
        log(s"pipeline $name not found")

        None
      } else {

        log(s"loaded pipeline: $name")

        Some(Pipeline(name))
      }
    }
  }

  private def loadPipelineContent(name: String): Future[Array[Byte]] = {
    Future {
      log(s"loading content for pipeline: $name")

      Thread.sleep(1500)

      log(s"loaded content for pipeline: $name")

      Array(43, 44, 45, 56)
    }
  }

  private def storePipeline(pipeline: Pipeline): StoredPipeline = {
    log(s"storing pipeline: $pipeline")

    val pipelineContent = Await.result(loadPipelineContent(pipeline.name), Duration.Inf)

    Thread.sleep(2000)

    log(s"stored pipeline: $pipeline")

    StoredPipeline(pipeline, "/shared/andrey.smirnov")
  }
}
