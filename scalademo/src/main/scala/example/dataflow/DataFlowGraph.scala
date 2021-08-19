package example.dataflow

import scala.collection.mutable

class DataFlowGraph(val pipeline: Pipeline) {
  private val liveIn = mutable.IndexedSeq.fill(pipeline.stages.size)(mutable.Set.empty[String])
  private val liveOut = mutable.IndexedSeq.fill(pipeline.stages.size)(mutable.Set.empty[String])

  private val successors = mutable.IndexedSeq.fill(pipeline.stages.size)(mutable.Set.empty[Int])

  private def getSubsequentStageIndices(stageIndex: Int): Set[Int] = {
    if (stageIndex == pipeline.stages.size - 1) Set.empty
    else {
      val stage = pipeline.stages(stageIndex)
      val outputDataset = stage.outputDataset

      // TODO should be expressed with pipelineStage.isDependsOn(outputDataset)
      pipeline.stages.zipWithIndex.filter {
        case (AggTransformer(inputDataset, _, _, _, _, _), _) => inputDataset == outputDataset
        case (NonAggTransformer(inputDataset, _, _, _, _, _, _), _) => inputDataset == outputDataset
        case (JoinTransformer(inputDatasets, _, _), _) => inputDatasets.contains(outputDataset)
        case (ScoringModelTransformer(inputDataset, _, _), _) => inputDataset == outputDataset
        case _ => false
      }.map(_._2).toSet
    }
  }

  def computeFlowGraph(): Unit = {
    val pipelineStages = pipeline.stages

    for (currentStageIndex <- pipelineStages.indices) {
      val currentStage = pipelineStages(currentStageIndex)

      val subsequentStageIndices = getSubsequentStageIndices(currentStageIndex)

      successors(currentStageIndex) ++= subsequentStageIndices
    }
  }

  def computeLivenessSets(): Unit = {
    val stages = pipeline.stages

    var liveOutChanged = true

    while (liveOutChanged) {
      liveOutChanged = false

      for (currentStageIndex <- stages.indices.reverse) {
        val currentStage = stages(currentStageIndex)
        // println("processing stage: " + currentStageIndex)

        val subsequentStages = findSubsequentStages(currentStageIndex)
        // println(s"subsequent stages: ${subsequentStages}");

        val currentStageLiveOut = liveOut(currentStageIndex)

        subsequentStages.foreach {
          case (subsequentStage, subsequentStageIndex) => {
            val subsequentStageLiveIn = liveIn(subsequentStageIndex)

            val diff = subsequentStageLiveIn diff currentStageLiveOut
            liveOutChanged |= !diff.isEmpty

            currentStageLiveOut ++= subsequentStageLiveIn
          }
        }

        val newLiveIn = currentStageLiveOut.clone()
        newLiveIn ++= currentStage.usedColumns

        liveIn(currentStageIndex) = newLiveIn
      }
    }
  }

  def optimizePipeline(): (Pipeline, Boolean) = {
    // TODO try to optimise backwards and drop optimized out columns from corresponding liveOut sets
    // basically, recompute liveness set for predecessor
    val optimizedPipelineStages = for {
      (currentStage, currentStageIndex) <- pipeline.stages
        .take(pipeline.stages.size - 1)
        .zipWithIndex

    } yield {
      // println("optimizing stage: " + currentStageIndex)

      val pipelineStageLiveColumns = liveOut(currentStageIndex)
      // println(s"pipeline stage ${currentStageIndex} live columns: ${pipelineStageLiveColumns}")

      val pipelineStageOutputColumns = currentStage.outputColumns
      // println(s"pipeline stage ${currentStageIndex} output columns: ${pipelineStageOutputColumns}")

      val liveColumnIndexes = pipelineStageOutputColumns.zipWithIndex.flatMap {
        case (outColName, outColIndex) =>
          if (pipelineStageLiveColumns.contains(outColName)) IndexedSeq(outColIndex)
          else IndexedSeq.empty
      }

      val optimized = !(pipelineStageOutputColumns.toSet diff pipelineStageLiveColumns.toSet).isEmpty
      (currentStage.selectColumns(liveColumnIndexes), optimized)
    }

    (Pipeline(optimizedPipelineStages.map(_._1) :+ pipeline.stages.last), optimizedPipelineStages.exists(_._2))
  }

  private def findSubsequentStages(currentStageIndex: Int): IndexedSeq[(Transformer, Int)] = {
    val subsequentStageIndexes = successors(currentStageIndex)
    val pipelineStages = pipeline.stages

    subsequentStageIndexes
      .map(stageIndex => (pipelineStages(stageIndex), stageIndex))
      .toIndexedSeq
  }
}
