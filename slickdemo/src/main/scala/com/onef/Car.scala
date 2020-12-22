package com.onef

// use postgres instead
import slick.jdbc.H2Profile.api._

import slick.lifted.ProvenShape

case class Car(
    name: String, 
    model: String, 
    id: Long = 0)

class MessageTable(tag: Tag) extends Table[Car](tag, "car") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def model = column[String]("model")

    override def * = (name, model, id).mapTo[Car]
}