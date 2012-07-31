package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.OrganizationForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.Organization
import jp.t2v.lab.play20.auth.LoginLogout
import org.colibrifw.controllers._

object OrganizationAdministration extends Controller with LoginLogout with AuthConfigImpl with LoginUser{

  val orgForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> optional(text)
    )(OrganizationForm.apply)(OrganizationForm.unapply)
  )
  def list = Action {
    val orgs=Organization.all()
    Ok(views.html.OrganizationAdministrationList(orgs))
  }
  def index = Action {
    Ok(views.html.OrganizationAdministrationCreate(orgForm))
  }
  def create = Action { implicit request =>
  	orgForm.bindFromRequest.fold(
	  errors => BadRequest(views.html.OrganizationAdministrationCreate(errors)),
	  orgs => {
		Organization.insert(orgs, loginUser.id.get) match {
          case 1 => Redirect(routes.OrganizationAdministration.list)
          case _ => BadRequest(views.html.OrganizationAdministrationCreate(orgForm))
		}
	  })
  }

  /*def create = TODO*/ /*Action { implicit request =>
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