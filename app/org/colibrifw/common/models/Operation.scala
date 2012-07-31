package org.colibrifw.common.models

import java.util.Date
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import org.colibrifw.common.forms.OperationForm

case class Operation(
    id:Pk[Int],
    identifier:String,
    menu_message:String,
    description:Option[String],
    order:Int,
    create_date:Date,
    update_date:Option[Date],
    update_user_id:Int,
    owner_organization_id:Int,
    status_id:Int
    ) extends Model {
  val update_user = User(update_user_id)
  val owner_organization = Organization(owner_organization_id)
  val status = Status(status_id)
}

object Operation {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("identifier") ~
    get[String]("menu_message") ~
    get[Option[String]]("description") ~
    get[Int]("order") ~
    get[Date]("create_date") ~
    get[Option[Date]]("update_date") ~
    get[Int]("update_user_id")~
    get[Int]("owner_organization_id")~
    get[Int]("status_id") map {
      case id~identifier~menu_message~description~order~
      create_date~update_date~update_user_id~owner_organization_id~status_id =>
        Operation(id,
            identifier,
            menu_message,
            description,
            order,
            create_date,
            update_date,
            update_user_id,
            owner_organization_id,
            status_id)
    }
  }
  def apply(id:Int):Option[Operation] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM OPERATION WHERE ID={id}").on("id" -> id).as(Operation.simple.singleOpt)
	}
  }
  def all(order:String="id"):Seq[Operation] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM Operation WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(Operation.simple *)
	}
  }
  def insert(opr:OperationForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    println("OperationForm=" + opr.toString())
    //TODO create_dateのトリガーがうまく動かないため一時的にinsert文で設定
    DB.withConnection { implicit c =>
      SQL("""
          insert into Operation (identifier, menu_message, description, Operation.order, create_date, update_user_id, owner_organization_id, status_id)
          values ({identifier}, {menu_message}, {description}, {order}, sysdate(), {update_user_id}, {owner_organization_id}, {status_id})
          """)
          .on("identifier" -> opr.identifier,
              "menu_message" -> opr.menu_message,
        	  "description" -> opr.description.getOrElse(""),
        	  "order" -> opr.order,
        	  "update_user_id" -> update_user_id,
        	  "owner_organization_id" -> opr.owner_organization_id,
        	  "status_id" -> 1)
              .executeUpdate()
    }
  }
}