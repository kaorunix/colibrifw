package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.LoginForm

object Info extends Controller {

  def index = Action {
    Ok(views.html.Info())
  }
}