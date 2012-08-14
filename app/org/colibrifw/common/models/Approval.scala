package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.i18n.Messages

case class Approval(
    id:Pk[Int],
    name:String,
    description:Option[String]
    ) extends Model {
}

object Approval {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("name") ~
    get[Option[String]]("description") map {
      case id~name~description => Approval(id, name, description)
    }
  }
  def apply(id:Int):Option[Approval] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Approval WHERE id={id}")
	  .on("id" -> id)
	  .as(Approval.simple.singleOpt)
	}
  }
  def all():Seq[Approval] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Approval order by id")
	  .as(Approval.simple *)
	}
  }

    // FormのSelect向けSeqを返却
  def allSelect():Seq[Pair[String, String]] = {
    all.map(a => (a.id.get.toString, Messages("Approval." + a.name)))
  }
}