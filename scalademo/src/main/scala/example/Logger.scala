package example

import java.text.SimpleDateFormat
import java.util.Date

object Logger {
  private val LogDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS")

  def log(message: String): Unit = {
    val currentThread = Thread.currentThread()

    val currentDate = new Date()

    println(s"${LogDateFormat.format(currentDate)} : [${currentThread.getName}] - $message")
  }
}
