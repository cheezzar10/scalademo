package example

object Sets {
    def main(args: Array[String]): Unit = {
        val pipelinesRenameMapping = Map(
            "dummy_score_1" -> Map("score" -> "score6", "val" -> "score7"),
            "dummy_score_3" -> Map("score_val" -> "score7"))

        // checking for pipeline local collisions
        for ((pipelineName, pipelineRenameMapping) <- pipelinesRenameMapping) {
            val finalColumnNames = pipelineRenameMapping.values
            val finalColumnNamesSet = pipelineRenameMapping.values.toSet

            if (finalColumnNames.size != finalColumnNamesSet.size) {
                println(s"final column names collision detected for pipeline $pipelineName")
                return ()
            }
        }

        // checking for cross pipeline collisions
        val (pipelinesFinalColumnNames, pipelinesFinalColumnNamesSet) = pipelinesRenameMapping.values.foldLeft((Seq.empty[String], Set.empty[String])) { 
            case ((finalColNames, finalColNamesSet), pipelineRenameMapping) => { 
                (finalColNames ++ pipelineRenameMapping.values, finalColNamesSet ++ pipelineRenameMapping.values) 
            }
        }

        if (pipelinesFinalColumnNames.size != pipelinesFinalColumnNamesSet.size) {
            println("cross pipeline rename mapping collision detected")
        }

        println("completed")
    }
}