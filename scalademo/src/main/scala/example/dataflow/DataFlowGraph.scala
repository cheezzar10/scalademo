package example.dataflow

import scala.collection.mutable

class DataFlowGraph(val pipeline: Pipeline) {
  private val liveIn = mutable.IndexedSeq.fill(pipeline.stages.size)(mutable.Set.empty[String])
  private val liveOut = mutable.IndexedSeq.fill(pipeline.stages.size)(mutable.Set.empty[String])

  def computeLivenessSets(): Unit = {
    val stages = pipeline.stages

    var liveOutChanged = true

    while (liveOutChanged) {
      liveOutChanged = false

      for (currentStageIndex <- stages.indices.reverse) {
        val currentStage = stages(currentStageIndex)
        println("processing stage: " + currentStageIndex)

        val nextStage = findNextStage(stages, currentStageIndex)
        println("next stage: " + nextStage)

        nextStage.foreach { stage =>
          val nextStageLiveIn = liveIn(currentStageIndex + 1)

          val currentStageLiveOut = liveOut(currentStageIndex)
          val diff = nextStageLiveIn diff currentStageLiveOut

          liveOutChanged = !diff.isEmpty

          currentStageLiveOut ++= nextStageLiveIn
        }

        val newLiveIn = liveOut(currentStageIndex).clone()
        newLiveIn ++= currentStage.usedColumns

        liveIn(currentStageIndex) = newLiveIn
      }
    }
  }

  def optimizePipeline(): Pipeline = {
    val optiizedPipelineStages = for (currentStageIndex <- 0 until (pipeline.stages.size - 1)) yield {
      println("optimizing stage: " + currentStageIndex)

      val pipelineStage = pipeline.stages(currentStageIndex)
      val pipelineStageLiveColumns = liveOut(currentStageIndex)

      println(s"pipeline stage ${currentStageIndex} live columns: ${pipelineStageLiveColumns}")

      val liveColumnIndexes = pipelineStage.outputColumns.zipWithIndex.flatMap {
        case (outColName, outColIndex) => if (pipelineStageLiveColumns.contains(outColName)) IndexedSeq(outColIndex) else IndexedSeq.empty
      }

      pipelineStage.selectColumns(liveColumnIndexes)
    }

    Pipeline(optiizedPipelineStages :+ pipeline.stages.last)
  }

  private def findNextStage(pipelineStages: IndexedSeq[Transformer], currentStageIndex: Int): Option[Transformer] = {
    val nextStageIndex = currentStageIndex + 1
    if (pipelineStages.isDefinedAt(nextStageIndex)) {
      Some(pipelineStages(nextStageIndex))
    } else {
      None
    }
  }
}
