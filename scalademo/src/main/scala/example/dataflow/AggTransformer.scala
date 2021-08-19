package example.dataflow

case class AggTransformer(
  inputDataset: String,
  inputColumns: IndexedSeq[String],
  groupByColumns: IndexedSeq[String],
  functions: IndexedSeq[AggregationFunction],
  outputDataset: String,
  outputColumns: IndexedSeq[String]) extends Transformer {

  def usedColumns: IndexedSeq[String] = inputColumns

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = {
    copy(
      inputColumns = select(inputColumns, liveColumnIndexes),
      functions = select(functions, liveColumnIndexes),
      outputColumns = select(outputColumns, liveColumnIndexes))
  }
}
