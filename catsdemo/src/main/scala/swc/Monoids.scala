package swc

import cats._
import cats.implicits._

case class Order(totalCost: Double, quantity: Double) {
    def total = totalCost * quantity
}

object Monoids {
    def main(args: Array[String]): Unit = {
        println(add(List(1, 2, 3)))

        val opts = List(Some(1), Some(3), None)
        println(add(opts))

        val orders = List(Order(1.0, 2.0), Order(4.0, 5.0))
        println(add(orders map { _.total }))
    }

    def add[A](items: List[A])(implicit m: Monoid[A]): A =
        items.foldLeft(m.empty)(m.combine)
}
