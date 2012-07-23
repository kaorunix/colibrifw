package org.colibrifw.controllers

import play.api._
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import org.colibrifw.common.forms.OperationForm
import org.colibrifw.common.utils._
import org.colibrifw.common.models.Operation
import jp.t2v.lab.play20.auth.LoginLogout

object OperationAdministration extends Controller with LoginLogout with AuthConfigImpl{

  val userForm = Form(
    mapping(
      "identifier" -> nonEmptyText,
	  "menu_message" -> nonEmptyText,
	  "description" -> optional(text),
	  "order" -> number,
	  "owner_organization_id" -> number
    )(OperationForm.apply)(OperationForm.unapply)
  )
  def index = Action {
    val orgs=Operation.all()
    Ok(views.html.OperationAdministrationList(orgs))
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