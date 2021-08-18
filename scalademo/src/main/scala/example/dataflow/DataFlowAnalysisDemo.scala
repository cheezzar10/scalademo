package example.dataflow

import ColumnScalarFunctions._

object DataFlowAnalysisDemo {
  def main(args: Array[String]): Unit = {
    val transformStage = new NonAggTransformer(
      inputColumns = IndexedSeq("feature31", "feature32"),
      outputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3"),
      functions = IndexedSeq(Some(If), Some(If)),
      functionNumArgs = IndexedSeq(IndexedSeq(3.0, 0.0), IndexedSeq(0.0, 181.0)),
      functionStrArgs = IndexedSeq(IndexedSeq.empty, IndexedSeq.empty))

    println("used columns: " + transformStage.usedColumns)

    val scoringStage = new ScoringModelTransformer(
      outputColumns = IndexedSeq("score"),
      featureNames = IndexedSeq("src2_feature31_agg5"))

    val pipeline = Pipeline(stages = IndexedSeq(transformStage, scoringStage))

    val dataFlowGraph = new DataFlowGraph(pipeline)
    dataFlowGraph.computeLivenessSets()

    // println("live out: " + dataFlowGraph.liveOut)

    val optimizedPipeline = dataFlowGraph.optimizePipeline()
    println("optimized pipeline: " + optimizedPipeline)

    outputColumnsFiltering()
  }

  private def outputColumnsFiltering(): Unit = {
    val outCols = IndexedSeq("feature1", "feature2", "feature3", "feature4")

    // TODO compute indexes of retained columns
    val liveOut = Set("feature2", "feature4")

    val retainedColIndexes = outCols.zipWithIndex.flatMap {
      case (outColName, outColIndex) => if (liveOut.contains(outColName)) IndexedSeq(outColIndex) else IndexedSeq.empty
    }

    println("retained column indexes: " + retainedColIndexes)

    val optimizedOutCols = select(outCols, retainedColIndexes)

    println("optimized columns set: " + optimizedOutCols)
  }

  private def select[A](indexedSeq: IndexedSeq[A], selectedIndexes: IndexedSeq[Int]): IndexedSeq[A] = {
    selectedIndexes.foldLeft(IndexedSeq.empty[A]) {
      case (selectedSeq, selectedIndex) => selectedSeq :+ indexedSeq(selectedIndex)
    }
  }
}
