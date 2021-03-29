package com.onef

import slick.jdbc.H2Profile.api._

case class Owner(firstName: String, lastName: String, id: Long = 0)

class Owners(tag: Tag) extends Table[Owner](tag, "owner") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  override def * = (firstName, lastName, id).mapTo[Owner]
}

object Owners {
  lazy val Owners = TableQuery[Owners]
}
