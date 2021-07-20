package example.lang

import scala.util.Try

object TryDemo {
  def main(args: Array[String]): Unit = {
    tryIntParsing()
  }

  private def tryIntParsing(): Unit = {
    val version = "2"

    val pipelineVersion = Try(version.toInt).getOrElse(1)

    print("pipeline version: " + pipelineVersion)
  }

  private def tryDemo(): Unit = {
    Try {
      println("computing")

      1
    }

    println("completed")
  }
}
