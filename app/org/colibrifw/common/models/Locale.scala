package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.i18n.Messages

case class Locale(
    id:Pk[Int],
    arconym:String,
    name:String
    ) extends Model {
}

object Locale {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("acronym") ~
    get[String]("name") map {
      case id~acronym~name => Locale(id, acronym, name)
    }
  }
  def apply(id:Int):Option[Locale] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Locale WHERE ID={id}").on("id" -> id).as(Locale.simple.singleOpt)
	}
  }
  def all():Seq[Locale] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Locale order by id").as(Locale.simple *)
	}
  }
  // FormのSelect向けSeqを返却
  def allSelect():Seq[Pair[String, String]] = {
    all.map(a => (a.id.get.toString, Messages("Locale." + a.arconym)))
  }
}