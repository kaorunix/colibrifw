package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.UserForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.User
import jp.t2v.lab.play20.auth.LoginLogout

object UserAdministration extends Controller with LoginLogout with AuthConfigImpl{

  val userForm = Form(
    mapping(
      "account" -> nonEmptyText,
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
  def index = Action {
    val users=User.all()
    Ok(views.html.UserAdministrationList(users))
  }
  def create = TODO /*Action { implicit request =>
  	userForm.bindFromRequest.fold(
    errors => BadRequest(views.html.UserAdministrationForm(errors)),
    login => {
      User.findUserByLoginForm(login) match {
        case Some(user:User) => {
          gotoLoginSucceeded(user.id.get)
          Ok(views.html.Info(login))
        }
       case _ => BadRequest(views.html.loginForm(loginForm))
      }
    })
  }*/
  def delete = TODO/*Action {implicit request =>
    gotoLogoutSucceeded
  }*/
}