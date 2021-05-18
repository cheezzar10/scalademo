package swc

import PrintableInstances._

final case class Cat(name:String, age: Int, color: String)

object Printables {
    implicit val catPrintable: Printable[Cat] = new Printable[Cat] {
        def format(v: Cat): String = s"${v.name} is a ${v.age} year-old ${v.color} cat"
    }

    def main(args: Array[String]): Unit = {
        println("started")

        Printable.print("hello")

        val cat = Cat("Murka", 1, "black")

        Printable.print(cat)
    }
}
