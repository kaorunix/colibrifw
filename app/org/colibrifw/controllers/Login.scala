package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.LoginForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.User
import jp.t2v.lab.play20.auth.LoginLogout

object Login extends Controller with LoginLogout with AuthConfigImpl{

  val loginForm = Form(
    mapping(
      "account" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )
  def index = Action {
    Ok(views.html.loginForm(loginForm))
  }
  def login = Action { implicit request =>
  	loginForm.bindFromRequest.fold(
    errors => BadRequest(views.html.loginForm(errors)),
    login => {
      User.findUserByLoginForm(login) match {
        case Some(user:User) => {
          gotoLoginSucceeded(user.id.get)
          Ok(views.html.Info(login))
        }
       case _ => BadRequest(views.html.loginForm(loginForm))
      }
    })
  }
  def logout = Action {implicit request =>
    gotoLogoutSucceeded
  }
}