package example.collections

import java.util.UUID

case class ProcessingStep(
                           sessionId: Option[String] = None,
                           datasetId: Option[String] = None) {

  def asMap(): Map[String, String] = {
    val vals = Seq(sessionId.map("sessionId" -> _), datasetId.map("datasetId" -> _))

    vals.flatten.toMap
  }
}

object FlattenDemo {
  def main(args: Array[String]): Unit = {
    println("started")

    val step = ProcessingStep(
      sessionId = Some(UUID.randomUUID().toString),
      datasetId = Some("some dataset id"))

    println("processing step: " + step)

    println("processing step as map: " + step.asMap)
  }
}
