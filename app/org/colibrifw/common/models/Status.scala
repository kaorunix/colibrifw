package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

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
}