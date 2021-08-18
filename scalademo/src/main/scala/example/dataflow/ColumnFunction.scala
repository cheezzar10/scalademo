package example.dataflow

sealed trait ColumnFunction {
  def name: String
}

trait ColumnScalarFunction extends ColumnFunction

object ColumnScalarFunctions {
  object If extends ColumnScalarFunction {
    val name = "if"
  }

  object In extends ColumnScalarFunction {
    val name = "in"
  }
}
