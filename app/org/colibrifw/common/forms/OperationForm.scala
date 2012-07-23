package org.colibrifw.common.forms

case class OperationForm(
		identifier:String,
		menu_message:String,
		description:Option[String],
		order:Int,
		owner_organization_id:Int
    ) extends Form {
}