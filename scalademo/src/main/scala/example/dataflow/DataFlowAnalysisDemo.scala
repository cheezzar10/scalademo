package example.dataflow

import RowFunctions._
import AggregationFunctions._

object DataFlowAnalysisDemo {
  def main(args: Array[String]): Unit = {
    val aggTransformer = AggTransformer(
      inputDataset = "features",
      inputColumns = IndexedSeq("feature1", "feature2"),
      groupByColumns = IndexedSeq("id"),
      functions = IndexedSeq(Mean, Mean),
      outputDataset = "features_direct",
      outputColumns = IndexedSeq("feature1_agg", "feature2_agg"))

    val nonAggTransformer = NonAggTransformer(
      inputDataset = "features",
      inputColumns = IndexedSeq("feature31", "feature32"),
      outputDataset = "features_transformed",
      outputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3"),
      functions = IndexedSeq(Some(If), Some(If)),
      functionNumArgs = IndexedSeq(IndexedSeq(3.0, 0.0), IndexedSeq(0.0, 181.0)),
      functionStrArgs = IndexedSeq(IndexedSeq.empty, IndexedSeq.empty))

    println("row transformer used columns: " + nonAggTransformer.usedColumns)

    val joinTransformer = JoinTransformer(
      inputDatasets = IndexedSeq("features_direct", "features_transformed"),
      joinColumns = IndexedSeq(IndexedSeq("id"), IndexedSeq("id")),
      outputDataset = "collected_features")

    val scoringStage = ScoringModelTransformer(
      inputDataset = "collected_features",
      outputColumns = IndexedSeq("score"),
      featureNames = IndexedSeq("feature1_agg", "src2_feature31_agg5"))

    val pipeline = Pipeline(stages = IndexedSeq(aggTransformer, nonAggTransformer, joinTransformer, scoringStage))

    val dataFlowGraph = new DataFlowGraph(pipeline)

    dataFlowGraph.computeFlowGraph()
    dataFlowGraph.computeLivenessSets()

    // println("live out: " + dataFlowGraph.liveOut)

    val optimizedPipeline = dataFlowGraph.optimizePipeline()
    println("optimized pipeline: " + optimizedPipeline)
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
