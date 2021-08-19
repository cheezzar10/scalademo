package example.dataflow

case class JoinTransformer(
  inputDatasets: IndexedSeq[String],
  joinColumns: IndexedSeq[IndexedSeq[String]],
  outputDataset: String) extends Transformer {

  def inputColumns: IndexedSeq[String] = IndexedSeq.empty

  def outputColumns: IndexedSeq[String] = IndexedSeq.empty

  def usedColumns: IndexedSeq[String] = IndexedSeq.empty

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = this
}
