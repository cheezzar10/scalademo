package example.dataflow

case class NonAggTransformer(
  inputDataset: String,
  inputColumns: IndexedSeq[String],
  outputDataset: String,
  outputColumns: IndexedSeq[String],
  functions: IndexedSeq[Option[RowFunction]],
  functionNumArgs: IndexedSeq[IndexedSeq[Double]],
  functionStrArgs: IndexedSeq[IndexedSeq[String]]) extends Transformer {

  def usedColumns: IndexedSeq[String] = {
    (for ((columnFunction, columnFunctionIndex) <- functions.zipWithIndex) yield {
      columnFunction match {
        case Some(function) => Some(inputColumns(columnFunctionIndex))
        case None => None
      }
    }).flatten
  }

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = {
    // println("selection: " + liveColumnIndexes)

    copy(
      inputColumns = select(inputColumns, liveColumnIndexes),
      outputColumns = select(outputColumns, liveColumnIndexes),
      functions = select(functions, liveColumnIndexes),
      functionNumArgs = select(functionNumArgs, liveColumnIndexes),
      functionStrArgs = select(functionStrArgs, liveColumnIndexes))
  }
}
