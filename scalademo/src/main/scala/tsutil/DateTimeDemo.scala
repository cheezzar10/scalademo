package tsutil

import java.time.format.DateTimeFormatter
import java.time.{ LocalDate, LocalDateTime, ZoneId }
import java.util.Date
import java.time.ZonedDateTime

object DateTimeDemo {
  private val DateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss_SSSSSS")
  private val RequestDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def main(args: Array[String]): Unit = {
    val currentTime = LocalDateTime.now()

    localDateTimeFormatting(currentTime)

    unixSeconds()
  }

  private def unixSeconds(): Unit = {
    val requestDate = "2020-11-12"

    val unixTime = LocalDate
      .parse(requestDate, RequestDateFormat)
      .atStartOfDay(ZoneId.systemDefault())
      .toInstant()
      .getEpochSecond()
      .toInt

    println("request date: " + new Date(unixTime * 1000L))
    println("unix time: " + unixTime)
  }

  private def localDateConstruction(): Unit = {
    // creating timestamp for dataset
    val partitionTime = LocalDate.of(2014, 1, 1).atTime(0, 0)

    val partitionTs = partitionTime.atZone(ZoneId.of("UTC"))
      .toInstant
      .toEpochMilli

    println("partition timestamp: " + partitionTs)

    val partitionDate = new Date(partitionTs)

    println("partition date: " + partitionDate)
  }

  private def localDateTimeFormatting(time: LocalDateTime): Unit = {
    println("time: " + time)
    println("time nanos: " + time.getNano)
    println("formatted time: " + time.format(DateTimeFormat))
  }
}
