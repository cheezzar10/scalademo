package example.dataflow

class ScoringModelTransformer(
  val outputColumns: IndexedSeq[String],
  val featureNames: IndexedSeq[String]) extends Transformer {

  def inputColumns: IndexedSeq[String] = featureNames

  def usedColumns: IndexedSeq[String] = featureNames

  def selectColumns(liveColumnIndexes: IndexedSeq[Int]): Transformer = this
}
