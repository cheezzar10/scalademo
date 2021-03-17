package example

import java.lang.reflect.InvocationTargetException

import org.apache.commons.lang3.exception.ExceptionUtils

object Launcher {
  def main(args: Array[String]): Unit = {
    val t = new Thread("app thread") {
      override def run(): Unit = {
        try launch()
        catch {
          case ite: InvocationTargetException => {
            val stackTraceAsStr = ExceptionUtils.getStackTrace(ite)
            // ite.getCause.printStackTrace()
            println(s"application class launch failed with exception:\n $stackTraceAsStr")
          }
        }
      }
    }

    t.start()

    t.join()
  }

  private def launch() {
    println("creating application class instance")

    val appClass = Class.forName("example.Hello")

    val mainMethod = appClass.getMethod("main", classOf[Array[String]])

    mainMethod.invoke(null, Array[String]())
  }
}
