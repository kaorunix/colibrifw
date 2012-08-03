package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.LoginForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.User
import jp.t2v.lab.play20.auth.LoginLogout
import cache.Cache
import Play.current
import views.html.login._
import views.html.info._

object Login extends Controller with LoginLogout with AuthConfigImpl with LoginUser{

  val loginForm = Form(
    mapping(
      "account" -> nonEmptyText,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
  )
  def index = Action {
    Ok(loginWebForm(loginForm))
  }
  def login = Action { implicit request =>
  	loginForm.bindFromRequest.fold(
    errors => BadRequest(loginWebForm(errors)),
    login => {
      User.findUserByLoginForm(login) match {
        case Some(user:User) => {
          gotoLoginSucceeded(user.id.get)
          // 自分を特定する
          Ok(InfoWeb(user)).withSession("loginUser" -> user.id.get.toString)
        }
       case _ => BadRequest(loginWebForm(loginForm))
      }
    })
  }
  def logout = Action {implicit request =>
    gotoLogoutSucceeded
  }
}