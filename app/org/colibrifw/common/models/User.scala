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
import org.colibrifw.common.exceptions.DaoException

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
    approval_id:Int,
    create_date:Date,
    update_date:Option[Date],
    update_user_id:Int,
    status_id:Int
    ) extends Model {
	def lang = Lang(lang_id)
	def timezone = TimeZone(timezone_id)
	def locale = Locale(locale_id)
	def country = Country(country_id)
	def approval = Approval(approval_id)
	def organization = Organization(organization_id)
	def update_user = User.findUserNameById(update_user_id)
	def status = Status(status_id)
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
    get[Int]("approval_id") ~
    get[Date]("create_date") ~
    get[Option[Date]]("update_date") ~
    get[Int]("update_user_id")~
    get[Int]("status_id") map {
      case id~account~name~description~password~organization_id
      ~lang_id~timezone_id~locale_id~country_id~approval_id
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
            approval_id,
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
  def all(order:String="id", asc:Boolean=true):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User WHERE status_id not in (5,6) order by " + order + {if (!asc) " DESC" else ""}).as(User.simple *)
	}
  }
  def allo(organization_id:Int, order:String="id"):Seq[User] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM User, Organization WHERE status_id not in ({status_id}) order by {order}").on("status_id" -> "5,6", "order" -> order).as(User.simple *)
	}
  }
  def insert(user:UserCreateForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    //ユーザから承認オプションを取得し、更新ステータスを設定する。
    //ユーザの承認オプションが1の場合、承認済みでデータを作成する。
    //ユーザの承認オプションが2,3の場合、未承認でデータを作成する。
    val status_id = this(update_user_id).getOrElse(throw new DaoException("20001",format("No exists User for user_id=%d", update_user_id)))
      .approval_id match {
        case 1 => 1
        case 2|3 => 2
        case _ => throw new DaoException("", "")
    }
    //TODO create_dateのトリガーがうまく動かないため一時的にinsert文で設定
    DB.withConnection { implicit c =>
      SQL("""
          insert into User (account, name, description, password, organization_id, lang_id, timezone_id, locale_id, country_id, approval_id, create_date, update_user_id, status_id)
          values ({account}, {name}, {description}, {password}, {organization_id}, {lang_id}, {timezone_id}, {locale_id}, {country_id},{approval_id}, sysdate(),{update_user_id}, {status_id})
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
        	  "approval_id" -> user.approval_id,
              "update_user_id" -> update_user_id,
              "status_id" -> status_id)
              .executeUpdate()
    }
  }
  def update(user:UserModifyForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    //ユーザから承認オプションを取得し、更新ステータスを設定する。
    //ユーザの承認オプションが1の場合、承認済みでデータを作成する。
    //ユーザの承認オプションが2,3の場合、未承認でデータを作成する。
    val status_id = this(update_user_id).getOrElse(throw new DaoException("20001",format("No exists Approve_Option for user_id=%d", update_user_id)))
      .approval_id match {
        case 1 => 1
        case 2|3 => 2
        case _ => throw new DaoException("","")
    }
    //条件idが編集対象ユーザ、status_id=1(approved)のもの
    DB.withConnection { implicit c =>
      SQL("""
          update User set
            account={account}, name={name}, description={description},
            organization_id={organization_id}, lang_id={lang_id},
            timezone_id={timezone_id}, locale_id={locale_id},
            country_id={country_id}, approval_id={approval_id},
            update_user_id={update_user_id},
            status_id={status_id}, update_date=sysdate()
            where id={id} and status_id = 1
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
        	  "approval_id" -> user.approval_id,
              "update_user_id" -> update_user_id,
              "status_id" -> status_id)
              .executeUpdate()
    }
  }
  def updateWithPassword(user:UserModifyForm, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    //ユーザから承認オプションを取得し、更新ステータスを設定する。
    //ユーザの承認オプションが1の場合、承認済みでデータを作成する。
    //ユーザの承認オプションが2,3の場合、未承認でデータを作成する。
    val status_id = this(update_user_id).getOrElse(throw new DaoException("20001",format("No exists Approve_Option for user_id=%d", update_user_id)))
      .approval_id match {
        case 1 => 1
        case 2|3 => 2
        case _ => throw new DaoException("","")
    }
    DB.withConnection { implicit c =>
      SQL("""
          update User set
            account={account}, name={name}, description={description},
            password={password},
            organization_id={organization_id}, lang_id={lang_id},
            timezone_id={timezone_id}, locale_id={locale_id},
            country_id={country_id}, approval_id={approval_id},
            update_user_id={update_user_id},
            status_id={status_id}, update_date=sysdate()
            where id ={id} and status_id=1
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
        	  "approval_id" -> user.approval_id,
              "update_user_id" -> update_user_id,
              "status_id" -> status_id)
              .executeUpdate()
    }
  }
  /**
   * 承認方式は
   * 1. 変更者と異なる人が承認を行うもの
   * 2. 変更者も承認できるもの
   * がある。
   */
  def updateForStatus(user_id:Int, approach:Pair[Seq[Int], Int], update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    lazy val exception = new DaoException("20001","NotExists")
   	this(update_user_id).getOrElse(throw exception).approval_id match {
    	// 自分のは承認できない。
   		case 3 =>{
   		  DB.withConnection { implicit c =>
   			SQL("""
   				update User set
   				update_user_id={update_user_id}, status_id={status_id}
   			    where id={id}
   			          and status_id in ({status_ids})
   				      and not update_user_id = {update_user_id}
   				""")
   				.on("id" -> user_id,
   					"update_user_id" -> update_user_id,
   					"status_ids" -> approach._1.mkString(","),
   					"status_id" -> approach._2)
   					.executeUpdate()
   		  }
   		}
   		case 1|2 => {
   		  //承認済=1にする
   		  DB.withConnection { implicit c =>
   			SQL("""
   				update User set
   				update_user_id={update_user_id}, status_id={status_id}
   			    where id={id} and status_id in ({status_ids})
   				""")
   				.on("id" -> user_id,
   					"update_user_id" -> update_user_id,
   					"status_ids" -> approach._1.mkString(","),
   					"status_id" -> approach._2)
   					.executeUpdate()
   		  }
   		}
   		case _ => throw new DaoException("","No exsits approval_option")
    }
  }
  def delete(user_id:Int, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    //ユーザから承認オプションを取得し、更新ステータスを設定する。
    //ユーザの承認オプションが1の場合、承認済みでデータを作成する。
    //ユーザの承認オプションが2,3の場合、未承認でデータを作成する。
    val status_id = this(update_user_id).getOrElse(throw new DaoException("20001",format("No exists Approve_Option for user_id=%d", update_user_id)))
      .approval_id match {
        case 1 => 6
        case 2|3 => 4
        case _ => throw new DaoException("","")
    }
    DB.withConnection { implicit c =>
      SQL("""
          update User set
            status_id={status_id} where id ={id} and status_id=1
          """)
          .on("id" -> user_id,
              "status_id" -> status_id)
              .executeUpdate()
    }
  }
  def approve(user_id:Int, update_user_id:Int):Int = {
    println("update_user_id=" + update_user_id)
    val user = User(user_id) match {
      case Some(u) => u
      case _ => throw new DaoException("", format("Not exsits user=%s", user_id))
    }
    val where_statement = this(update_user_id)
    .getOrElse(throw new DaoException("20001",format("No exists Approve_Option for user_id=%d", update_user_id)))
      .approval_id match {
        case 1|2 => " and update_user_id={update_user_id}"
        case 3 => ""
    }
    val status_id:Int = user.status_id match {
      case 2|3 => 1 // 2:作成、3:変更を1:承認へ変える
      case 4 => 6   // 4:削除 を 6:削除済みへ変える
      case _ => throw new DaoException("","")
    }
    //ユーザから承認オプションを取得し、更新ステータスを設定する。
    //ユーザの承認オプションが1,2の場合、承認済みにデータを更新する。
    //ユーザの承認オプションが3の場合、更新者が自分でない場合は承認済みにデータを更新する。
    DB.withConnection { implicit c =>
      SQL("""
       	update User set
        status_id={status_id}, update_user_id={update_user_id}
        where id ={id} and status_id={current_status_id}
        """ + where_statement)
          .on("id" -> user_id,
              "current_status_id" -> user.status_id,
              "status_id" -> status_id,
              "update_user_id" -> update_user_id)
              .executeUpdate()
    }
  }
}
