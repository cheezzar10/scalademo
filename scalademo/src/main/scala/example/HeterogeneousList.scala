package example

sealed trait HeterogeneousList {
    def concat[H](head: H): NonEmptyHeterogeneousList[H, this.type] = NonEmptyHeterogeneousList(head, this)
}

case class NonEmptyHeterogeneousList[+H, T <: HeterogeneousList](head: H, tail: T) extends HeterogeneousList {
    override def toString = s"$head --> $tail"
}

trait EmptyHeterogeneousList extends HeterogeneousList

case object EmptyHeterogeneousList extends EmptyHeterogeneousList

case class Row(id: Long, name: String, age: Int)

object HeterogeneousList {
    implicit def convert[A, B, C](tuple: (A, B, C)): NonEmptyHeterogeneousList[A, NonEmptyHeterogeneousList[B, NonEmptyHeterogeneousList[C, EmptyHeterogeneousList]]] = {
        NonEmptyHeterogeneousList(tuple._1, NonEmptyHeterogeneousList(tuple._2, NonEmptyHeterogeneousList(tuple._3, EmptyHeterogeneousList)))
    }

    def main(args: Array[String]): Unit = {
        println("started")

        val row = Row(1L, "Billi Bones", 32)

        println("row " + row)

        val rowAsList: NonEmptyHeterogeneousList[Long, NonEmptyHeterogeneousList[String, NonEmptyHeterogeneousList[Int, EmptyHeterogeneousList]]] =
            Row.unapply(row).get
    }
}
