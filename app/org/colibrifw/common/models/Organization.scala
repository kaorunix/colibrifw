package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import java.util.Date

case class Organization(
		id:Pk[Int],
		name:String,
		description:Option[String],
		create_date:Date,
		update_date:Option[Date],
		update_user_id:Int,
		status_id:Int
	) extends Model {
  //val update_user = User(update_user_id)
  val status = Status(status_id)

}

object Organization {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("name") ~
    get[Option[String]]("description") ~
    get[Date]("create_date") ~
    get[Option[Date]]("update_date") ~
    get[Int]("update_user_id")~
    get[Int]("status_id") map {
      case id~name~description~create_date~update_date~update_user_id~status_id =>
        Organization(id, name, description, create_date, update_date, update_user_id, status_id)
    }
  }
  def apply(id:Int):Option[Organization] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Organization WHERE ID={id}").on("id" -> id).as(Organization.simple.singleOpt)
	}
  }
  def all(order:String="id"):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Organization WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(User.simple *)
	}
  }
}