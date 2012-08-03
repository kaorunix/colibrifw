package org.colibrifw.common.models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import org.colibrifw.common._
import forms.LoginForm
import java.util.Date
import org.colibrifw.common.utils.Encryption
import org.colibrifw.common.forms.UserCreateForm
import org.colibrifw.common.forms.UserModifyForm
import anorm.NotAssigned
import org.colibrifw.common.exceptions.IllegalParameterException

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
	def lang = Lang(lang_id)
	def timezone = TimeZone(timezone_id)
	def locale = Locale(locale_id)
	def country = Country(country_id)
	def organization = Organization(organization_id)
	def status = Status(status_id)
	def update_user = User.findUserNameById(update_user_id)
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

  def findUserNameById(id:Int):Option[String] = {
	/*val user = DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE ID={id}").on("id" -> id).as(User.simple.singleOpt)
	}*/
	apply(id) match {
	  case Some(u) => Some(u.name)
	  case _ => None
	}
  }
  def findUserByLoginForm(login:LoginForm):Option[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE account={account} AND password={password_encoded}")
  	    .on("account" -> login.account, "password_encoded" -> Encryption.encript(login.password))
	    .as(User.simple.singleOpt)
	}
  }
  def findUserByUserCreateForm(user:UserCreateForm):Option[User] = {
    findUserByAccount(user.account)
  }
  def findUserByAccount(account:String):Option[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE account={account}").on("account" -> account).as(User.simple.singleOpt)
	}
  }
  def findUserByAccount(id:Int, account:String):Option[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE account={account} and not id={id}").on("account" -> account, "id" -> id).as(User.simple.singleOpt)
	}
  }
  def all(order:String="id"):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(User.simple *)
	}
  }
  def allo(organization_id:Int, order:String="id"):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User, Organization WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(User.simple *)
	}
  }
  def insert(user:UserCreateForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    //TODO create_dateのトリガーがうまく動かないため一時的にinsert文で設定
    DB.withConnection { implicit c =>
      SQL("""
          insert into User (account, name, description, password, organization_id, lang_id, timezone_id, locale_id, country_id, create_date, update_user_id, status_id)
          values ({account}, {name}, {description}, {password}, {organization_id}, {lang_id}, {timezone_id}, {locale_id}, {country_id},sysdate(),{update_user_id}, {status_id})
          """)
          .on("account" -> user.account,
        	  "name" -> user.name,
        	  "description" -> user.description,
        	  "password" -> Encryption.encript(user.password.main),
        	  "organization_id" -> user.organization_id,
        	  "lang_id" -> user.lang_id,
        	  "timezone_id" -> user.timezone_id,
        	  "locale_id" -> user.locale_id,
        	  "country_id" -> user.country_id,
              "update_user_id" -> update_user_id,
              "status_id" -> 1)
              .executeUpdate()
    }
  }
  def update(user:UserModifyForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    DB.withConnection { implicit c =>
      SQL("""
          update User set
            account={account}, name={name}, description={description},
            organization_id={organization_id}, lang_id={lang_id},
            timezone_id={timezone_id}, locale_id={locale_id},
            country_id={country_id}, update_user_id={update_user_id},
            status_id={status_id} where id={id}
          """)
          .on("id" -> user.account.id,
              "account" -> user.account.account,
        	  "name" -> user.name,
        	  "description" -> user.description,
        	  "organization_id" -> user.organization_id,
        	  "lang_id" -> user.lang_id,
        	  "timezone_id" -> user.timezone_id,
        	  "locale_id" -> user.locale_id,
        	  "country_id" -> user.country_id,
              "update_user_id" -> update_user_id,
              "status_id" -> 1)
              .executeUpdate()
    }
  }
  def updateWithPassword(user:UserModifyForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    DB.withConnection { implicit c =>
      SQL("""
          update User set
            account={account}, name={name}, description={description},
            password={password},
            organization_id={organization_id}, lang_id={lang_id},
            timezone_id={timezone_id}, locale_id={locale_id},
            country_id={country_id}, update_user_id={update_user_id},
            status_id={status_id} where id ={id}
          """)
          .on("id" -> user.account.id,
              "account" -> user.account.account,
        	  "name" -> user.name,
        	  "description" -> user.description,
        	  "password" -> {
        	    user.password.main match {
        	      case Some(pass:String) => Encryption.encript(pass)
        	      case _ => throw new IllegalParameterException("10001","password in not entered")
        	    }
        	  },
        	  "organization_id" -> user.organization_id,
        	  "lang_id" -> user.lang_id,
        	  "timezone_id" -> user.timezone_id,
        	  "locale_id" -> user.locale_id,
        	  "country_id" -> user.country_id,
              "update_user_id" -> update_user_id,
              "status_id" -> 1)
              .executeUpdate()
    }
  }
}
