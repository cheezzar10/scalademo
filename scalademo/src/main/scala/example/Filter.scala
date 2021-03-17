package example

object Filter {
    class Table(val columns: Seq[String]) {
        def renameColumn(oldName: String, newName: String): Table = {
            println(s"renaming column $oldName -> $newName")

            val newNames = columns.map(n => if (n == oldName) newName else n)

            new Table(newNames)
        }

        override def toString = columns.toString
    }

    def main(args: Array[String]): Unit = {
        val table = new Table(List("msisdn_hash", "request_date"))

        val processedTable = performProcessing(table) {
            (resultTable, colName) => {
                resultTable.renameColumn(colName, colName + "_v1")
            }
        }

        println(s"processed table: $processedTable")
    }

    private def performProcessing(table: Table)(processor: (Table, String) => Table): Table = {
        table.columns.foldLeft(table)(processor)
    }
}