package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import java.util.Date

case class TimeZone(
    id:Pk[Int],
    acronym:String,
    name:String,
    hour:Int,
    summer_time_begin:Option[Date],
    summer_time_end:Option[Date]
    ) extends Model {
}

object TimeZone {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("acronym") ~
    get[String]("name") ~
    get[Int]("hour") ~
    get[Option[Date]]("summer_time_begin") ~
    get[Option[Date]]("summer_time_end") map {
      case id~acronym~name~hour~summer_time_begin~summer_time_end =>
        TimeZone(id, acronym, name, hour, summer_time_begin, summer_time_end)
    }
  }
  def apply(id:Int):Option[TimeZone] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM TimeZone WHERE ID={id}").on("id" -> id).as(TimeZone.simple.singleOpt)
	}
  }
}