package example.dataflow

import scala.collection.mutable

class PipelineTranslator(val pipeline: Pipeline) {
  private val datasetsTable: mutable.Map[String, DatasetDescriptor] = mutable.Map.empty

  def translate(): IndexedSeq[String] = {
    // resolving datasets and output column transformation expressions
    for (pipelineStage <- pipeline.stages) {
      pipelineStage match {
        case aggTransformerStage: AggTransformer => translateAggTransformerStage(aggTransformerStage)
        case nonAggTransformerStage: NonAggTransformer => translateNonAggTransformerStage(nonAggTransformerStage)
        case joinTransformerStage: JoinTransformer => translateJoinTransformerStage(joinTransformerStage)
        case _ => {}
      }
    }

    val lastTransformStage = pipeline.stages.reverse.find(t => !t.isInstanceOf[ScoringModelTransformer]).get
    datasetsTable(lastTransformStage.outputDataset).translatedOutputColumns
  }

  private def translateAggTransformerStage(aggTransformer: AggTransformer): Unit = {
    val inputDatasetName = aggTransformer.inputDataset
    val inputColumnNames = aggTransformer.inputColumns
    val outputDatasetName = aggTransformer.outputDataset
    val outputColumnNames = aggTransformer.outputColumns

    println(s"aggregating transformer translation - ${aggTransformer}")

    val inputDatasetOpt = datasetsTable.get(inputDatasetName)

    if (inputDatasetOpt.isEmpty) {
      // using input columns literally
      println(s"aggregating transformer input dataset '${inputDatasetName}' is not found")

      val translatedOutputColumns = (inputColumnNames, outputColumnNames, aggTransformer.functions).zipped
        .map {
          case (inputColumnName, outputColumnName, aggregationFunction) =>
            outputColumnName -> s"${inputColumnName}>${aggregationFunction.name}"
        }.toMap

      println(s"fresh dataset translated output columns: ${translatedOutputColumns}")

      datasetsTable(outputDatasetName) = DatasetDescriptor(
        name = outputDatasetName,
        inputColumns = inputColumnNames,
        outputColumns = outputColumnNames,
        translatedOutputColumns = outputColumnNames.map(translatedOutputColumns))

    } else {
      val inputDataset = inputDatasetOpt.get

      println(s"aggregating transformer input dataset '${inputDatasetName}' found: ${inputDataset}")

      val translatedOutputColumns = inputDataset.translatedOutputColumns
        .zip(inputDataset.outputColumns)
        .zip(aggTransformer.functions)
        .map {
          case ((upstreamTransformation, outputColumnName), aggregationFunction) =>
            outputColumnName -> s"${upstreamTransformation}|${aggregationFunction.name}"
        }.toMap

      println(s"existing dataset translated output columns: ${translatedOutputColumns}")

      datasetsTable(outputDatasetName) = DatasetDescriptor(
        name = outputDatasetName,
        inputColumns = inputColumnNames,
        outputColumns = outputColumnNames,
        translatedOutputColumns = outputColumnNames.map(translatedOutputColumns))
    }
  }

  def translateNonAggTransformerStage(nonAggTransformer: NonAggTransformer): Unit = {
    println(s"non-aggregation transformer translation - ${nonAggTransformer}")

    val inputDatasetName = nonAggTransformer.inputDataset
    val inputColumnNames = nonAggTransformer.inputColumns
    val outputDatasetName = nonAggTransformer.outputDataset
    val outputColumnNames = nonAggTransformer.outputColumns

    val inputDatasetOpt = datasetsTable.get(inputDatasetName)

    if (inputDatasetOpt.isEmpty) {
      println(s"non-aggregating transformer input dataset '${inputDatasetName}' not found")

      val translatedOutputColumns = inputColumnNames
        .zip(outputColumnNames)
        .zip(nonAggTransformer.functionsWithArgs)
        .map {
          case ((inputColumnName, outputColumnName), (transformationFunction, transformationFunctionNumArgs, transformationFunctionStrArgs)) => {
            val translatedTransformationFunction = transformationFunction.translateToString(transformationFunctionNumArgs, transformationFunctionStrArgs)
            outputColumnName -> s"$inputColumnName>$translatedTransformationFunction"
          }
        }.toMap

      datasetsTable(outputDatasetName) = DatasetDescriptor(
        name = outputDatasetName,
        inputColumns = inputColumnNames,
        outputColumns = outputColumnNames,
        translatedOutputColumns = outputColumnNames.map(translatedOutputColumns))

    } else {
      val inputDataset = inputDatasetOpt.get

      println(s"non-aggregating transformer input dataset found: ${inputDataset}")
    }
  }

  private def translateJoinTransformerStage(joinTransformer: JoinTransformer): Unit = {
    val inputDatasetNames = joinTransformer.inputDatasets
    val outputDatasetName = joinTransformer.outputDataset

    println(s"join tranformer translation - ${joinTransformer}")

    val inputDatasets = inputDatasetNames.flatMap { inputDatasetName =>
      datasetsTable.get(inputDatasetName)
    }

    val joinedInputColumns = inputDatasets.flatMap(_.inputColumns)
    val joinedOutputColumns = inputDatasets.flatMap(_.outputColumns)
    val joinedTranslatedOutputColumns = inputDatasets.flatMap(_.translatedOutputColumns)

    datasetsTable(outputDatasetName) = DatasetDescriptor(
      name = outputDatasetName,
      inputColumns = joinedInputColumns,
      outputColumns = joinedOutputColumns,
      translatedOutputColumns = joinedTranslatedOutputColumns)
  }
}
