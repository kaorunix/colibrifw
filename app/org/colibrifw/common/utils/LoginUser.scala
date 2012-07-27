package org.colibrifw.common.utils

import org.colibrifw.common.models.User
import play.api.mvc.Session
import play.api.mvc.Request
import play.api.mvc.Controller
trait LoginUser {
  def loginUser[T](implicit request:Request[T]):User = User(request.session.get("loginUser").get.toInt).get
  //override def Ok(content:)
}