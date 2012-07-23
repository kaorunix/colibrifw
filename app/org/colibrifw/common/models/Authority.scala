package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Authority(
    id:Pk[Int],
    organization_id:Int,
    order:Int,
    operation_id:Int) extends Model {
}

object Authority {
  val simple = {
    get[Pk[Int]]("id") ~
    get[Int]("organization_id") ~
    get[Int]("order") ~
    get[Int]("operation_id") map {
      case id~organization_id~order~operation_id =>
        Authority(id,organization_id,order,operation_id)
    }
  }
  def apply(id:Int):Option[Authority] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Authority WHERE ID={id}").on("id" -> id).as(Authority.simple.singleOpt)
	}
  }
}