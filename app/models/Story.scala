package models

import play.api.libs.json._

case class Story(number: String, title: String, phase: String)

object Story {  
  implicit val storyFormat = Json.format[Story]
}
