package example.json

import java.nio.file.{ Files, Paths }

import org.json4s.{ DefaultFormats, Formats }
import org.json4s.jackson.JsonMethods

case class Pipeline(stages: List[PipelineStage])

case class PipelineStage(className: String, params: List[StageParam])

case class StageParam(name: String, value: List[String])

object PipelineParsingDemo {
  private implicit val JsonFormats: Formats = DefaultFormats

  private lazy val userHome = sys.props("user.home")

  def main(args: Array[String]): Unit = {
    val jsonPath = Paths.get(userHome, "Downloads", "enclave-pipeline.js")
    val json = Files.newInputStream(jsonPath)

    val pipeline = JsonMethods.parse(json).extract[Pipeline]
    println("pipeline: " + pipeline)
  }
}
