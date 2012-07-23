package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Permission(
    id:Pk[Int],
    user_id:Int,
    admin:Int,
    approve:Int,
    modifyOther:Int,
    modify:Int,
    reference:Int
    ) extends Model {
}

object Permission {
  val simple = {
    get[Pk[Int]]("id") ~
    get[Int]("user_id") ~
    get[Int]("admin") ~
    get[Int]("approve") ~
    get[Int]("modifyOther") ~
    get[Int]("modify") ~
    get[Int]("reference") map {
      case id~user_id~admin~approve~modifyOther~modify~reference =>
        Permission(id, user_id, admin, approve, modifyOther, modify, reference)
    }
  }
  def apply(id:Int):Option[Permission] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Permission WHERE ID={id}").on("id" -> id).as(Permission.simple.singleOpt)
	}
  }
  /*def all():Seq[Permission] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Permission order by id").as(Permission.simple)
	}
  }*/
}