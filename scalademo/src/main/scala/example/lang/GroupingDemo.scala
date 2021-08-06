package example.lang

import scala.collection.mutable
import scala.collection.mutable

case class Field(name: String)

object GroupingDemo {
  def main(args: Array[String]): Unit = {
    val fields = Seq(Field("msisdn_hash"), Field("request_date"), Field("filter1"), Field("msisdn_hash"))

    // manualGrouping(fields)

    val groups = groupReduce(fields)(_.name)((x1, x2) => x2)
    val uniqueFields = groups.values.toList

    println(s"unique fields: $uniqueFields")

    val uniqueFieldsSeq: Seq[Field] = uniqueFields
    println(s"unique fields sequence: $uniqueFieldsSeq")
  }

  private def manualGrouping(fields: Seq[Field]): Unit = {
    val result = fields.groupBy(_.name)
      .map {
        case (fieldName, fields) => (fieldName, fields.head)
      }
    println(s"result: $result")

    val uniqueFields = mutable.LinkedHashSet(fields: _*)
    println(s"unique fields: $uniqueFields")

    val correctResult = uniqueFields.map(f => result(f.name))
    println(s"correct result: $correctResult")
  }

  private def groupReduce[A, B](xs: Seq[A])(key: A => B)(reduce: (A, A) => A): mutable.Map[B, A] = {
    val groups = new mutable.LinkedHashMap[B, A]()

    for (x <- xs) {
      val groupKey = key(x)

      groups(groupKey) = groups.get(groupKey) match {
        case Some(x1) => reduce(x1, x)
        case None => x
      }
    }

    groups
  }
}
