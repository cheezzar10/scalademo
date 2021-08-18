package example.dataflow

case class NonAggTransformer(
  val inputColumns: IndexedSeq[String],
  val outputColumns: IndexedSeq[String],
  val functions: IndexedSeq[Option[ColumnScalarFunction]],
  val functionNumArgs: IndexedSeq[IndexedSeq[Double]],
  val functionStrArgs: IndexedSeq[IndexedSeq[String]]) extends Transformer {

  def usedColumns: IndexedSeq[String] = {
    // add all columns serving as function inputs

    (for ((columnFunction, columnFunctionIndex) <- functions.zipWithIndex) yield {
      columnFunction match {
        case Some(function) => Some(inputColumns(columnFunctionIndex))
        case None => None
      }
    }).flatten
  }

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = {
    println("selection: " + liveColumnIndexes)
    copy(
      inputColumns = select(inputColumns, liveColumnIndexes),
      outputColumns = select(outputColumns, liveColumnIndexes),
      functions = select(functions, liveColumnIndexes),
      functionNumArgs = select(functionNumArgs, liveColumnIndexes),
      functionStrArgs = select(functionStrArgs, liveColumnIndexes))
  }
}
