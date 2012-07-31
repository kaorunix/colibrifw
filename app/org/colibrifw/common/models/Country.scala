package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.i18n.Messages

case class Country(
    id:Pk[Int],
    acronym:String,
    name:String
    ) extends Model {
}

object Country {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("acronym") ~
    get[String]("name") map {
      case id~acronym~name=> Country(id, acronym, name)
    }
  }
  def apply(id:Int):Option[Country] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Country WHERE ID={id}").on("id" -> id).as(Country.simple.singleOpt)
	}
  }
  def all():Seq[Country] = {
   	DB.withConnection { implicit c =>
   	  SQL("SELECT * FROM Country order by id").as(Country.simple *)
   	}
  }
  // FormのSelect向けSeqを返却
  def allSelect():Seq[Pair[String, String]] = {
    all.map(a => (a.id.get.toString, Messages("Country." + a.acronym)))
  }

}