package example

import scala.reflect.runtime.universe._

import scala.language.experimental.macros

object MacroDemo {
  def main(args: Array[String]): Unit = {
    println("started")

    val cClass = q"case class C(s: String)"

    val cCode = showCode(cClass)

    println("code: " + cCode)

    val cRaw = showRaw(cClass)

    println("raw: " + cRaw)

    // val s = Macros.uppercase("foo")

    // println("s = " + s)
  }
}
