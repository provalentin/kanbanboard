package models

import play.api.libs.json._

case class Story(id: Long, title: String, phase: String)

object Story {  
  implicit val storyFormat = Json.format[Story]
}
