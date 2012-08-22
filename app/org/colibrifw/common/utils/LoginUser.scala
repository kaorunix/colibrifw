package org.colibrifw.common.utils

import org.colibrifw.common.models.User
import play.api.mvc.Session
import play.api.mvc.Request
import play.api.mvc.Controller
import play.api.i18n.Lang

trait LoginUser extends Controller{
  def loginUser[T](implicit request:Request[T]):User = {
    User(request.session.get("loginUser").get.toInt).get
  }
}