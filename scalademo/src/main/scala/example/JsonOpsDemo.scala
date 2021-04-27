package example

import java.util.UUID

import org.json4s.jackson.JsonMethods._

import org.json4s.{Formats, ShortTypeHints}
import org.json4s.jackson.Serialization

sealed trait ProcessingStep {
  def copyValues(values: Map[String, _]): ProcessingStep
}

case class StartSessionStep(sessionId: Option[String] = None) extends ProcessingStep {
  override def copyValues(values: Map[String, _])= {
    copy(sessionId = values.get("sessionId").map(_.asInstanceOf[String]))
  }
}

case class DatasetLoadStep(
                            inputPath: String,
                            datasetId: Option[String] = None,
                            sessionId: Option[String] = None) extends ProcessingStep {
  override def copyValues(values: Map[String, _]) = {
    copy(
      datasetId = values.get("datasetId").map(_.asInstanceOf[String]),
      sessionId = values.get("sessionId").map(_.asInstanceOf[String]))
  }
}

case class ProcessingSteps(steps: List[ProcessingStep])

case class ProcessingStepMaps(steps: List[Map[String, _]])

object JsonOpsDemo {
  private implicit val Formats: Formats = Serialization.formats(ShortTypeHints(List(
    classOf[StartSessionStep], classOf[DatasetLoadStep])))

  def main(args: Array[String]): Unit = {
    println("performing case class serialization")

    val step = StartSessionStep(sessionId = Some(UUID.randomUUID().toString))

    val stepAsStr = Serialization.write(step)

    println("start session step json: " + stepAsStr)

    val steps = ProcessingSteps(List(step))

    val processingStepJsonStr = Serialization.write(steps)

    println("processing steps json: " + processingStepJsonStr)

    val stepJson = parse(stepAsStr)

    val stepAsMap = stepJson.extract[Map[String, _]]

    println("processing step map: " + stepAsMap)

    val datasetLoadStep = DatasetLoadStep("/shared/andrey/input")
      .copyValues(stepAsMap - "jsonClass")

    val datasetLoadStepJsonStr = Serialization.write(datasetLoadStep)

    println("dataset load step json: " + datasetLoadStepJsonStr)

    val datasetLoadStepJson = parse(datasetLoadStepJsonStr)
    val datasetLoadStepJsonMap = datasetLoadStepJson.extract[Map[String, _]] ++ (stepAsMap - "jsonClass")

    println("dataset load step json map: " + datasetLoadStepJsonMap)

    val modifiedDatasetLoadStepJsonStr = Serialization.write(ProcessingStepMaps(List(datasetLoadStepJsonMap)))

    println("modified dataset load step json: " + modifiedDatasetLoadStepJsonStr)

    val modifiedDatasetLoadStepJson = parse(modifiedDatasetLoadStepJsonStr)

    val modifiedSteps = modifiedDatasetLoadStepJson.extract[ProcessingSteps]

    println("modified processing steps: " + modifiedSteps)
  }
}
