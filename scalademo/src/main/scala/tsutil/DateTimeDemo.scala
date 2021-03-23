package tsutil

import java.time.{LocalDate, ZoneId}
import java.util.Date

object DateTimeDemo {
  def main(args: Array[String]): Unit = {
    // creating timestamp for dataset
    val partitionTime = LocalDate.of(2014, 1, 1).atTime(0, 0)

    val partitionTs = partitionTime.atZone(ZoneId.of("UTC"))
      .toInstant
      .toEpochMilli

    println("partition timestamp: " + partitionTs)

    val partitionDate = new Date(partitionTs)

    println("partition date: " + partitionDate)
  }
}
