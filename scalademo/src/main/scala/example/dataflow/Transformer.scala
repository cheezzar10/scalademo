package example.dataflow

trait Transformer {
  def inputColumns: IndexedSeq[String]

  def outputColumns: IndexedSeq[String]

  def outputDataset: String

  def usedColumns: IndexedSeq[String]

  def definedColumns: IndexedSeq[String] = outputColumns

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer

  def select[A](indexedSeq: IndexedSeq[A], selectedIndexes: IndexedSeq[Int]): IndexedSeq[A] = {
    // println(s"$indexedSeq selected indexes: $selectedIndexes")

    selectedIndexes.foldLeft(IndexedSeq.empty[A]) {
      case (selectedSeq, selectedIndex) => selectedSeq :+ indexedSeq(selectedIndex)
    }
  }
}
