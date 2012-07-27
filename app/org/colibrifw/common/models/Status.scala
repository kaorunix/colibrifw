package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.i18n.Messages

case class Status(
    id:Pk[Int],
    name:String,
    description:Option[String]) extends Model {
}

object Status {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("name") ~
    get[Option[String]]("description") map {
      case id~name~description => Status(id, name, description)
    }
  }
  def apply(id:Int):Option[Status] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM STATUS WHERE ID={id}").on("id" -> id).as(Status.simple.singleOpt)
	}
  }
  def all():Seq[Status] = {
   	DB.withConnection { implicit c =>
   	  SQL("SELECT * FROM Status order by id").as(Status.simple *)
   	}
  }
  // FormのSelect向けSeqを返却
  def allSelect():Seq[Pair[String, String]] = {
    all.map(a => (a.id.get.toString, Messages("Status." + a.name)))
  }
}