package example

import org.json4s.Formats
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import scala.util.Try
import scala.util.Failure

object JsDemo {
  private implicit val JsonFormats: Formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val json = "{}"

    val parsedJson = JsonMethods.parse(json)
    println("parsed json: " + parsedJson)

    val attrs = parsedJson.extract[Map[String, String]]
    println("attrs: " + attrs)

    val row: Map[String, Any] = Map("foo" -> "bar", "baz" -> None)
    println("field: " + row.get(""))

    val str: String = getVal(row)("foo").get
    println("str: " + str)
  }

  private def getVal[A](row: Map[String, Any])(fieldName: String): Try[A] = {
      row.get(fieldName) match {
        case None => Failure(new IllegalArgumentException(s"invalid field name: '$fieldName'"))
        case Some(v) => v match {
          case None => Failure(new IllegalArgumentException(s"field '$fieldName' value set to None"))
          case v => Try(v.asInstanceOf[A])
        }
      }
  }
}
