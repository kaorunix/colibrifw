package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.LoginForm

object Login extends Controller {

  val loginForm = Form(
    mapping(
      "account" -> text,
      "password" -> text
    )(LoginForm.apply)(LoginForm.unapply)
  )
  def index = Action {
    Ok(views.html.loginForm(loginForm))
  }
  def login = Action { implicit request =>
  	loginForm.bindFromRequest.fold(
    errors => BadRequest(views.html.loginForm(errors)),
    login => {
      Ok(views.html.result(login))
    })
  }
}