package tictactoe.persistence.databaseAccess.slick

import slick.jdbc.H2Profile.api._
import slick.lifted.{ProvenShape}

import scala.language.postfixOps

/*
class Users(tag: Tag) extends Table[(UserId, String, String, Boolean, String, String)](tag, "users") {
  def id = column[Int]("id", O.PrimaryKey) // This is the primary key column
  def name = column[String]("name")
  def email = column[String]("email")
  def emailConfirmed = column[String]("emailConfirmed")
  def key = column[String]("key")
  def passwordHash = column[String]("passwordHash")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, email, emailConfirmed, key, passwordHash)
}*/
private class Users(tag: Tag) extends Table[(String, String, String, Boolean, String, String)](tag, "users") {
  def _id: Rep[String] = column[String]("_id", O.PrimaryKey) // This is the primary key column
  def name: Rep[String] = column[String]("name")
  def email: Rep[String] = column[String]("email")
  def emailConfirmed: Rep[Boolean] = column[Boolean]("emailConfirmed")
  def key: Rep[String] = column[String]("key")
  def passwordHash: Rep[String] = column[String]("passwordHash")


  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String, String, Boolean, String, String)] =
    (_id, name, email, emailConfirmed, key, passwordHash)
}
