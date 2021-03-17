package example

import scala.reflect.macros.Context

import scala.language.experimental.macros

// TODO move macro definitions to another compilation unit
object Macros {
  def uppercaseImpl(c: Context)(strExpr: c.Expr[String]): c.Expr[String] = {
    import c.universe._

    val Literal(Constant(str: String)) = strExpr.tree

    c.Expr[String](q"${str.toUpperCase}")
  }

  def uppercase(strExpr: String): String = macro uppercaseImpl
}
