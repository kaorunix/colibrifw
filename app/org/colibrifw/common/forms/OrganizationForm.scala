package org.colibrifw.common.forms

case class OrganizationForm(
		name:String,
		description:Option[String]
    ) extends Form {
}