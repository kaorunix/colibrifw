package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.LoginForm
import jp.t2v.lab.play20.auth.LoginLogout
import org.colibrifw.common.utils.AuthConfigImpl
import play.api.cache.Cache
import play.api.Play.current
import org.colibrifw.common.utils.LoginUser

object Info extends Controller with LoginLogout with AuthConfigImpl with LoginUser{

  def index = Action { request =>
    Ok(views.html.info.InfoWeb(loginUser(request)))
  }
}