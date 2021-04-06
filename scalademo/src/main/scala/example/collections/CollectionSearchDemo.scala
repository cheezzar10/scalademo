package example.collections

object CollectionSearchDemo {
  def main(args: Array[String]): Unit = {
    val emptySeq = Seq.empty[String]

    val result = emptySeq.forall(_.contains("A"))

    println("result: " + result)
  }
}
