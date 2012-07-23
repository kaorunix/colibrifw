package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import org.colibrifw.common._
import forms.LoginForm
import java.util.Date
import org.colibrifw.common.utils.Encryption

case class User(
    id:Pk[Int],
    account:String,
    name:String,
    description:Option[String],
    password:String,
    organization_id:Int,
    lang_id:Int,
    timezone_id:Int,
    locale_id:Int,
    country_id:Int,
    create_date:Date,
    update_date:Option[Date],
    status_id:Int,
    update_user_id:Int
    ) extends Model {
	val lang = Lang(lang_id)
	val timezone = TimeZone(timezone_id)
	val locale = Locale(locale_id)
	val country = Country(country_id)
	val organization = Organization(organization_id)
}

object User {
  val simple = {
    get[Pk[Int]]("id") ~
    get[String]("account") ~
    get[String]("name") ~
    get[Option[String]]("description") ~
    get[String]("password") ~
    get[Int]("organization_id") ~
    get[Int]("lang_id") ~
    get[Int]("timezone_id") ~
    get[Int]("locale_id") ~
    get[Int]("country_id") ~
    get[Date]("create_date") ~
    get[Option[Date]]("update_date") ~
    get[Int]("update_user_id")~
    get[Int]("status_id") map {
      case id~account~name~description~password~organization_id~lang_id~timezone_id~locale_id~country_id
      ~create_date~update_date~update_user_id~status_id =>
        User(id,
            account,
            name,
            description,
            password,
            organization_id,
            lang_id,
            timezone_id,
            locale_id,
            country_id,
            create_date,
            update_date,
            update_user_id,
            status_id)
    }
  }
  def apply(id:Int):Option[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE ID={id}").on("id" -> id).as(User.simple.singleOpt)
	}
  }
  def findUserByLoginForm(login:LoginForm):Option[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE account={account} AND password={password_encoded}").on("account" -> login.account, "password_encoded" -> Encryption.encript(login.password)).as(User.simple.singleOpt)
	}
  }
  def all(order:String="id"):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(User.simple *)
	}
  }
}
