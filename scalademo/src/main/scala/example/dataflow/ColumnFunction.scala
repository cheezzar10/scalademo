package example.dataflow

sealed trait ColumnFunction {
  def name: String

  override def toString: String = s"function: '$name'"
}

trait RowFunction extends ColumnFunction

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
