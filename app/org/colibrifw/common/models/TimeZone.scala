package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import java.util.Date
import play.api.i18n.Messages

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
    def all():Seq[TimeZone] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM TimeZone order by id").as(this.simple *)
	}
  }
  // FormのSelect向けSeqを返却
  def allSelect():Seq[Pair[String, String]] = {
    all.map(a => (a.id.get.toString, Messages("TimeZone." + a.name)))
  }
}