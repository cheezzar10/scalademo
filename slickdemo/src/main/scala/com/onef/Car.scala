package com.onef

// use postgres instead

import java.sql.Blob

import slick.jdbc.H2Profile.api._

case class Car(
                name: String,
                model: String,
                image: Blob,
                ownerId: Long,
                id: Long = 0)

// TODO basically this class should be named Cars
class CarTable(tag: Tag) extends Table[Car](tag, "car") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def name = column[String]("name")

  def model = column[String]("model")

  def image = column[Blob]("image")

  def ownerId = column[Long]("owner_id")

  override def * = (name, model, image, ownerId, id).mapTo[Car]

  def owner = foreignKey("owner_fk", ownerId, Owners.Owners)(_.id)
}

object Cars {
  lazy val Cars = TableQuery[CarTable]
}
