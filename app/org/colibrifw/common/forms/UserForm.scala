package org.colibrifw.common.forms
import org.colibrifw.common.models.User
import org.colibrifw.common.exceptions.NotFoundException

case class UserCreateForm(
	account: String = "",
	name:String = "",
	description:Option[String] = Some("") ,
	password: UserPasswordForm,
	organization_id: Int = 1,
	lang_id: Int = 2,
	timezone_id:Int = 2,
	locale_id:Int = 2,
	country_id:Int = 2
  ) extends Form {
}

case class UserModifyForm(
    account:AccountForm,
	name:String = "",
	description:Option[String] = Some("") ,
	password: UserPasswordFormOp,
	organization_id: Int = 1,
	lang_id: Int = 2,
	timezone_id:Int = 2,
	locale_id:Int = 2,
	country_id:Int = 2
  ) extends Form {
}

object UserModifyForm {
  def getUserModifyFormById(id:Int) = {
    User(id) match {
      case Some(user:User) => UserModifyForm(
          AccountForm(user.id.get,user.account),
          user.name,
          user.description,
          UserPasswordFormOp(None, None),
          user.organization_id,
          user.lang_id,
          user.timezone_id,
          user.locale_id,
          user.country_id
          )
      case _ => throw new NotFoundException("10001", format("Not Found User by id=%d", id))
    }
  }
}

case class UserPasswordForm(
    main:String,
    confirm:String
  ) extends Form {
}
case class UserPasswordFormOp(
    main:Option[String],
    confirm:Option[String]
  ) extends Form {
}

case class AccountForm(
    id:Int,
    account:String
  ) extends Form {
}

