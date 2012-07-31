package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation._
import org.colibrifw.common.forms.UserForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.User
import jp.t2v.lab.play20.auth.LoginLogout

object UserAdministration extends Controller with LoginLogout with AuthConfigImpl with LoginUser{
  val userForm = Form(
    mapping(
      "account" -> nonEmptyText.verifying(
          Constraints.pattern("""[a-zA-Z0-9\.\_\-]+[\@]+[a-zA-Z0-9\.\_\-]+""".r),
          Constraint[String]("User.account.exists") {
            case (a:String) if (User.findUserByAccount(a) != None) =>
              Invalid(ValidationError("User.error.account.exists"))
            case _ => Valid
          }
        ),
      "name" -> nonEmptyText,
      "description" -> optional(text),
      "password" -> nonEmptyText,
      "password_confirm" -> nonEmptyText,
      "organization_id" -> number,
      "lang_id" -> number,
      "timezone_id" -> number,
      "locale_id" -> number,
      "country_id" -> number
    )(UserForm.apply)(UserForm.unapply)
  )
  def list = Action {
    val users=User.all()
    Ok(views.html.UserAdministrationList(users))
  }
  def index = Action {
    Ok(views.html.UserAdministrationCreate(userForm))
  }
  def create = Action { implicit request =>
  	userForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.UserAdministrationCreate(errors)),
	  user => {
		User.insert(user, loginUser.id.get) match {
          case 1 => Redirect(routes.UserAdministration.list)
          case _ => BadRequest(views.html.UserAdministrationCreate(userForm))
		}
	  })
  }
  def modifyById(id:String) = Action {
	Ok(views.html.UserAdministrationModify(userForm.fill(mkUserForm(id.toInt))))
  }
  def modify() = Action { implicit request =>
    userForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.UserAdministrationModify(errors)),
	  user => {
	    val r = if (user.password == "") {
		  User.update(user, loginUser.id.get)
	    } else {
		  User.updateWithPassword(user, loginUser.id.get)
		}
	    r match {
		  case 1 => Redirect(routes.UserAdministration.list)
		  case _ => BadRequest(views.html.UserAdministrationModify(userForm))
		}
	  }
    )
  }
  def mkUserForm(id:Int):UserForm = {
    User(id) match {
      case Some(user) => UserForm(
          user.account,
          user.name,
          Some(user.description.getOrElse("")),
          "",
          "",
          user.organization_id,
          user.lang_id,
          user.timezone_id,
          user.locale_id,
          user.country_id)
      case _ => UserForm()
    }
  }
  def delete = TODO/*Action {implicit request =>
    gotoLogoutSucceeded
  }*/
}