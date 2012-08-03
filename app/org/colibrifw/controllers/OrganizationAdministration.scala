package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.{OrganizationCreateForm,OrganizationModifyForm, OrganizationIdForm}
import org.colibrifw.common.utils._
import org.colibrifw.common.models.Organization
import jp.t2v.lab.play20.auth.LoginLogout
import org.colibrifw.controllers._
import views.html.organization._

object OrganizationAdministration extends Controller with LoginLogout with AuthConfigImpl with LoginUser{

  val orgCreateForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "description" -> optional(text)
    )(OrganizationCreateForm.apply)(OrganizationCreateForm.unapply)
  )
  val orgModifyForm = Form(
    mapping(
      "organization" -> mapping(
          "id" -> number,
          "name" -> nonEmptyText)
          (OrganizationIdForm.apply)(OrganizationIdForm.unapply),
      "description" -> optional(text))
    (OrganizationModifyForm.apply)(OrganizationModifyForm.unapply)
  )
  def list = Action {
    val orgs=Organization.all()
    Ok(OrganizationAdministrationList(orgs))
  }
  def index = Action {
    Ok(OrganizationAdministrationCreate(orgCreateForm))
  }
  def create = Action { implicit request =>
  	orgCreateForm.bindFromRequest.fold(
	  errors => BadRequest(OrganizationAdministrationCreate(errors)),
	  orgs => {
		Organization.insert(orgs, loginUser.id.get) match {
          case 1 => Redirect(routes.OrganizationAdministration.list)
          case _ => BadRequest(OrganizationAdministrationCreate(orgCreateForm))
		}
	  })
  }
  def modifyById(id:String) = Action {
	Ok(OrganizationAdministrationModify(
		orgModifyForm.fill(OrganizationModifyForm.getOrganizationModifyFormById(id.toInt))))
  }
  def modify() = Action { implicit request =>
    orgModifyForm.bindFromRequest.fold(
	  errors => BadRequest(OrganizationAdministrationModify(errors)),
	  organization => {
		val r = Organization.update(organization, loginUser.id.get)
	    r match {
		  case 1 => Redirect(routes.OrganizationAdministration.list)
		  case _ => BadRequest(OrganizationAdministrationModify(orgModifyForm))
		}
	  }
    )
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