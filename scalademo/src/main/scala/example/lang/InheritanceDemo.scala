package example.lang

sealed trait ProcessingStep {
  val uuid: String
}

class UuidIdentifiable(val uuid: String)

case class RunPipelineStep(
                            override val uuid: String,
                            pipelineName: String,
                            input: String) extends UuidIdentifiable(uuid) with ProcessingStep {

}

object InheritanceDemo {
  def main(args: Array[String]): Unit = {

  }
}
