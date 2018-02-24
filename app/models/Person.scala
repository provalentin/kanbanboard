package models

import play.api.libs.json._

case class Person(name: String, age: Int)

object Person{
  implicit val personFormat = Json.format[Person]
}
