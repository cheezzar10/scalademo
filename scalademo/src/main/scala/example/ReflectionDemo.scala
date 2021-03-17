package example

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.{universe => ru}

case class Car(maker: String, model: String)

object ReflectionDemo {
  def main(args: Array[String]): Unit = {
    val car = Car("BMW", "320i")

    println(s"car: $car")

    val carType = getTypeTag(car).tpe

    println(s"type tag: $carType")

    val carConstructor = carType.decl(ru.termNames.CONSTRUCTOR)

    println(s"car constructor: $carConstructor")

    val cstrParams = carConstructor.asMethod.paramLists.head

    val paramNames = cstrParams.map(_.name.toString)
    val paramValues = car.productIterator.map(_.toString).toList

    val mapEntries = ArrayBuffer[(String, String)]()
    for ((name, value) <- paramNames.zip(paramValues)) {
      mapEntries += name -> value
    }

    val map = mapEntries.toMap

    println(s"map: $map")
  }

  def getTypeTag[T : ru.TypeTag](obj: T) = ru.typeTag[T]
}
