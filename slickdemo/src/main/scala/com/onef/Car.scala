package com.onef

// use postgres instead

import java.sql.Blob

import slick.jdbc.H2Profile.api._

case class Car(
                name: String,
                model: String,
                image: Blob,
                id: Long = 0)

// TODO basically this class should be named Cars
class CarTable(tag: Tag) extends Table[Car](tag, "car") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def model = column[String]("model")

  def image = column[Blob]("image")

  override def * = (name, model, image, id).mapTo[Car]
}
