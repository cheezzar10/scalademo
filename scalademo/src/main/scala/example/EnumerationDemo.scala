package example

object JobType extends Enumeration {
  type JobType = Value

  val Batch, Score = Value
}

object EnumerationDemo {
  def main(args: Array[String]): Unit = {
    val jobType = identifyJobType("some param")

    val date = "2021-04-02"

    val rv = jobType match {
      case Right(jt @ JobType.Score) if date.isEmpty =>
        throw new IllegalArgumentException(s"date parameter is mandatory for job: $jt")
      case Right(jt) => {
        println("job type: " + jt.toString.toLowerCase)
        jt
      }
      case Left(errMsg) =>
        throw new IllegalArgumentException(errMsg)
    }

    println(s"return value: $rv")
  }

  def identifyJobType(param: String): Either[String, JobType.Value] = {
    Right(JobType.Score)
  }
}
