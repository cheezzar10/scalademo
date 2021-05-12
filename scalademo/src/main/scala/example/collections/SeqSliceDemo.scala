package example.collections

import scala.util.Try

object SeqSliceDemo {
  def main(args: Array[String]): Unit = {
    val row = Seq[Any]("aaa", 1, Array(1, 2))
    println("row: " + row)

    val featuresVector = row(2)
    println("features vector: " + featuresVector)

    val maybeFeaturesVector = getByIndexUsingSlice(row, -1)
    println("maybe features vector: " + maybeFeaturesVector)

    val optionalFeaturesVector = getByIndexUsingTry(row, -1)
    println("optional features vector: " + optionalFeaturesVector)
  }

  private def getByIndexUsingSlice(row: Seq[Any], index: Int) = {
    row.slice(index, index + 1).headOption
  }

  private def getByIndexUsingTry(row: Seq[Any], index: Int) = {
    Try(row(index)).toOption
  }
}
