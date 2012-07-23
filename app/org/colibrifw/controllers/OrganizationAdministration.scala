package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.OrganizationForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.Organization
import jp.t2v.lab.play20.auth.LoginLogout

object OrganizationAdministration extends Controller with LoginLogout with AuthConfigImpl{

  val userForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> optional(text)
    )(OrganizationForm.apply)(OrganizationForm.unapply)
  )
  def index = Action {
    val orgs=Organization.all()
    Ok(views.html.OrganizationAdministrationList(orgs))
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