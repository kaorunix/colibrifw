package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import org.colibrifw.common.forms.UserCreateForm
import org.colibrifw.common.forms.UserModifyForm
import org.colibrifw.common.forms.UserPasswordForm
import org.colibrifw.common.forms.UserPasswordFormOp
import org.colibrifw.common.forms.AccountForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.User
import jp.t2v.lab.play20.auth.LoginLogout

object UserAdministration extends Controller with LoginLogout with AuthConfigImpl with LoginUser{
  val userCreateForm = Form(
    mapping(
      "account" -> email.verifying(
/*          Constraints.pattern("""[a-zA-Z0-9\.\_\-]+[\@]+[a-zA-Z0-9\.\_\-]+""".r),*/
          Constraint[String]("User.account.exists") {
            case (a:String) if (User.findUserByAccount(a) != None) =>
              Invalid(ValidationError("User.error.account.exists"))
            case _ => Valid
          }
        ),
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "password" -> mapping(
        "main" -> nonEmptyText,
        "confirm" -> nonEmptyText)
        (UserPasswordForm.apply)(UserPasswordForm.unapply)
        .verifying("User.error.password.notmatch", pass => pass.main == pass.confirm),
      "organization_id" -> number,
      "lang_id" -> number,
      "timezone_id" -> number,
      "locale_id" -> number,
      "country_id" -> number,
      "approval_id" -> number
    )(UserCreateForm.apply)(UserCreateForm.unapply)
  )
  val userModifyForm = Form(
    mapping(
      "account" -> mapping(
        "id" -> number,
        "account" -> email.verifying(
          Constraints.pattern("""[a-zA-Z0-9\.\_\-]+[\@]+[a-zA-Z0-9\.\_\-]+""".r)

        ))
        (AccountForm.apply)
        (AccountForm.unapply).verifying(
          Constraint[AccountForm]("User.account.exists") {
            case (a:AccountForm) if (User.findUserByAccount(a.id, a.account) != None) =>
              Invalid(ValidationError("User.error.account.exists"))
            case _ => Valid
          }
        ),
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "password" -> mapping(
        "main" -> optional(nonEmptyText),
        "confirm" -> optional(nonEmptyText))
        (UserPasswordFormOp.apply)(UserPasswordFormOp.unapply)
        .verifying("User.error.password.notmatch", pass => pass.main == pass.confirm),
      "organization_id" -> number,
      "lang_id" -> number,
      "timezone_id" -> number,
      "locale_id" -> number,
      "country_id" -> number,
      "approval_id" -> number
    )
    (UserModifyForm.apply)
    (UserModifyForm.unapply)
  )
  def list = Action {
    val users=User.all()
    Ok(views.html.user.UserAdministrationList(users))
  }
  def index = Action {
    Ok(views.html.user.UserAdministrationCreate(userCreateForm))
  }
  def create = Action { implicit request =>
  	userCreateForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.user.UserAdministrationCreate(errors)),
	  user => {
		User.insert(user, loginUser.id.get) match {
          case 1 => Redirect(routes.UserAdministration.list)
          case _ => BadRequest(views.html.user.UserAdministrationCreate(userCreateForm))
		}
	  })
  }
  def modifyById(id:String) = Action {
	Ok(views.html.user.UserAdministrationModify(userModifyForm.fill(UserModifyForm.getUserModifyFormById(id.toInt))))
  }
  def modify() = Action { implicit request =>
    userModifyForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.user.UserAdministrationModify(errors)),
	  user => {
	    val r = if (user.password == "") {
		  User.update(user, loginUser.id.get)
	    } else {
		  User.updateWithPassword(user, loginUser.id.get)
		}
	    r match {
		  case 1 => Redirect(routes.UserAdministration.list)
		  case _ => BadRequest(views.html.user.UserAdministrationModify(userModifyForm))
		}
	  }
    )
  }
 def deleteById(id:String) = Action {
   User(id.toInt) match {
     case Some(user) => Ok(views.html.user.UserAdministrationDelete(user))
     case _ => Redirect(routes.UserAdministration.list)
   	}
  }
  def delete() = Action { implicit request =>
    val params:Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded
    val id = params.get("id").head
    User.delete(id.toInt, loginUser.id.get) match {
	  case 1 => Redirect(routes.UserAdministration.list)
	  case _ => Redirect(routes.UserAdministration.list)
	}
  }
 def approveById(id:String) = Action {
   User(id.toInt) match {
     case Some(user) => Ok(views.html.user.UserAdministrationApprove(user))
     case _ => Redirect(routes.UserAdministration.list)
   	}
  }
  def approve() = Action { implicit request =>
    val params:Option[Map[String, Seq[String]]] = request.body.asFormUrlEncoded
    val id = params.get("id").head
    User.approve(id.toInt, loginUser.id.get) match {
	  case 1 => Redirect(routes.UserAdministration.list)
	  case _ => Redirect(routes.UserAdministration.list)
	}
  }
}