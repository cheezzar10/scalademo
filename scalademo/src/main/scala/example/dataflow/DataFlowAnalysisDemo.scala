package example.dataflow

import RowFunctions._
import AggregationFunctions._

object DataFlowAnalysisDemo {
  def main(args: Array[String]): Unit = {
    val pipeline = createPipeline()

    def loop(inputPipeline: Pipeline, wasOptimized: Boolean): Pipeline = {
      if (!wasOptimized) inputPipeline
      else {
        val dataFlowGraph = new DataFlowGraph(inputPipeline)
        dataFlowGraph.computeFlowGraph()
        dataFlowGraph.computeLivenessSets()

        val (optimizedPipeline, optimized) = dataFlowGraph.optimizePipeline()

        loop(optimizedPipeline, optimized)
      }
    }

    val finalPipeline = loop(pipeline, true)
    println(s"final pipeline: ${finalPipeline}")

    val pipelineTranslator = new PipelineTranslator(finalPipeline)
    val translatedFeatures: IndexedSeq[String] = pipelineTranslator.translate()

    println(s"translated features: ${translatedFeatures}")
  }

  private def createPipeline(): Pipeline = {
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

    val circles0JoinTransformer = JoinTransformer(
      inputDatasets = IndexedSeq("circles_0", "features_direct", "features_transformed"),
      joinColumns = IndexedSeq(IndexedSeq("id"), IndexedSeq("id"), IndexedSeq("id")),
      outputDataset = "circle0_with_features")

    val circles4JoinTransformer = JoinTransformer(
      inputDatasets = IndexedSeq("circles_4", "features_direct", "features_transformed"),
      joinColumns = IndexedSeq(IndexedSeq("id"), IndexedSeq("id"), IndexedSeq("id")),
      outputDataset = "circle4_with_features")

    val circle0AggTransformer = AggTransformer(
      inputDataset = "circle0_with_features",
      inputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3", "feature1_agg", "feature2_agg"),
      groupByColumns = IndexedSeq("id"),
      functions = IndexedSeq(Mean, Mean, Mean, Mean),
      outputDataset = "circle0_aggregated",
      outputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3", "feature1_agg", "feature2_agg"))

    val circle4AggTransformer = AggTransformer(
      inputDataset = "circle4_with_features",
      inputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3", "feature1_agg", "feature2_agg"),
      groupByColumns = IndexedSeq("id"),
      functions = IndexedSeq(Mean, Mean, Mean, Mean),
      outputDataset = "circle4_aggregated",
      outputColumns = IndexedSeq("src2_feature31_agg5", "src2_feature32_agg3", "feature1_agg", "feature2_agg"))

    val joinTransformer = JoinTransformer(
      inputDatasets = IndexedSeq("sample", "features_direct", "circle0_aggregated", "circle4_aggregated"),
      joinColumns = IndexedSeq(IndexedSeq("id"), IndexedSeq("id"), IndexedSeq("id"), IndexedSeq("id")),
      outputDataset = "collected_features")

    val scoringStage = ScoringModelTransformer(
      inputDataset = "collected_features",
      outputColumns = IndexedSeq("score"),
      featureNames = IndexedSeq("feature1_agg", "src2_feature31_agg5"))

    Pipeline(stages = IndexedSeq(
      aggTransformer,
      nonAggTransformer,
      circles0JoinTransformer,
      circles4JoinTransformer,
      circle0AggTransformer,
      circle4AggTransformer,
      joinTransformer,
      scoringStage))
  }
}
