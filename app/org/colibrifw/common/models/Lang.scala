package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Lang(
    id:Pk[Int],
    acronym:String,
    name:String,
    description:Option[String]) extends Model {
}

object Lang {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("acronym") ~
    get[String]("name") ~
    get[Option[String]]("description") map {
      case id~acronym~name~description => Lang(id, acronym, name, description)
    }
  }
  def apply(id:Int):Option[Lang] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM LANG WHERE ID={id}").on("id" -> id).as(Lang.simple.singleOpt)
	}
  }
  /*def all():Seq[Locale] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Locale order by id").as(Locale.simple)
	}
  }*/

}