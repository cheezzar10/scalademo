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

        insertNewCars(db)

        // TODO load records from DB
        selectAllCars(db)

        selectCarByModel(db, "320d")
    }

    private def selectCarByModel(db: Database, model: String): Option[Car] = {
        println(s"selecting record from CAR table by model = $model")

        val cars = TableQuery[CarTable]

        // === generated by api.columnExtensionMethods
        // right hand side ( model argument ) converted using api.valueToConstColumn
        val selectionAction = cars
            .filter(c => c.model === model)
            .result

        Await.result(db.run(selectionAction), Duration.Inf)
            .headOption
    }

    private def selectAllCars(db: Database): Unit = {
        println("selecting all records from CAR table")

        val cars = TableQuery[CarTable]

        val selectedCars = Await.result(db.run(cars.result), Duration.Inf)

        println(s"selected cars: $selectedCars")
    }

    private def insertNewCars(db: Database): Unit = {
        println("inserting new records to CAR table")

        // TODO can be expressed as val cars = TableQuery[Cars]
        val car = TableQuery[CarTable]

        val insert = car ++= Seq(Car("BMW", "320d"), Car("BMW", "325d"))

        val insertActionResult = Await.result(db.run(insert), Duration.Inf)

        println("records added to CAR table")

    }

    private def createCarTable(db: Database): Unit = {
        println("creating CAR table")

        val carTable = TableQuery[CarTable]

        val createCarTableActionResult = Await.result(db.run(carTable.schema.create), Duration.Inf)

        println(s"CAR table creation result: $createCarTableActionResult")
    }

    private def tupledCreation(): Car = {
        val carTuple = ("BMW", "320d", 1L)

        val car = Car.tupled(carTuple)

        println(s"car: $car")

        car
    }
}