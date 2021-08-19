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
    val optimizedPipelineStages = for {
      (currentStage, currentStageIndex) <- pipeline.stages
        .take(pipeline.stages.size - 1)
        .zipWithIndex

    } yield {
      println("optimizing stage: " + currentStageIndex)

      val pipelineStageLiveColumns = liveOut(currentStageIndex)

      println(s"pipeline stage ${currentStageIndex} live columns: ${pipelineStageLiveColumns}")

      val liveColumnIndexes = currentStage.outputColumns.zipWithIndex.flatMap {
        case (outColName, outColIndex) =>
          if (pipelineStageLiveColumns.contains(outColName)) IndexedSeq(outColIndex)
          else IndexedSeq.empty
      }

      currentStage.selectColumns(liveColumnIndexes)
    }

    Pipeline(optimizedPipelineStages :+ pipeline.stages.last)
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
