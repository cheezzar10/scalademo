package example.dataflow

sealed trait ColumnFunction {
  def name: String

  override def toString: String = s"function: '$name'"
}

// to class, with name field
trait RowFunction extends ColumnFunction {
  def translateToString(numArgs: IndexedSeq[Double], strArgs: IndexedSeq[String]): String = {
    s"""$name@${numArgs.mkString(",")}"""
  }
}

object RowFunctions {
  object If extends RowFunction {
    val name = "if"
  }

  object In extends RowFunction {
    val name = "in"
  }
}

trait AggregationFunction extends ColumnFunction

object AggregationFunctions {
  object Mean extends AggregationFunction {
    val name = "mean"
  }
}
