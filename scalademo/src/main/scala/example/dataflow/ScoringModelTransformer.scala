package example.dataflow

case class ScoringModelTransformer(
  inputDataset: String,
  outputColumns: IndexedSeq[String],
  featureNames: IndexedSeq[String]) extends Transformer {

  def inputColumns: IndexedSeq[String] = featureNames

  def usedColumns: IndexedSeq[String] = featureNames

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = this
}
