package org.colibrifw.common.forms

case class UserForm(
		account: String,
		name:String,
		description:Option[String],
		password: String,
		password_confirm: String,
		organization_id: Int,
		lang_id: Int,
		timezone_id:Int,
		locale_id:Int,
		country_id:Int
    ) extends Form {
}