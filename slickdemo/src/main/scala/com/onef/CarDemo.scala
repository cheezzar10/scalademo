package com.onef

import slick.jdbc.H2Profile.backend.{Database}

// use postgres instead
import slick.jdbc.H2Profile.api._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

object CarDemo {
    def main(args: Array[String]): Unit = {
        val newCar = tupledCreation()

        // loading DB config stored in Typesafe Config format
        val db = Database.forConfig("car")

        // TODO abstract over database type ( see ES 5.1.1 )
        createCarTable(db)
    }

    private def createCarTable(db: Database): Unit = {
        println("creating CAR table")

        val carTable = TableQuery[CarTable]
        val createCarTableAction = carTable.schema.create
        val createCarTableActionResult = Await.result(db.run(createCarTableAction), Duration.Inf)

        println(s"CAR table creation result: $createCarTableActionResult")
    }

    private def tupledCreation(): Car = {
        val carTuple = ("BMW", "320d", 1L)

        val car = Car.tupled(carTuple)

        println(s"car: $car")

        car
    }
}