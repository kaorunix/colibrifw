package org.colibrifw.common.forms

case class UserForm(
		account: String = "",
		name:String = "",
		description:Option[String] = Some("") ,
		password: String = "",
		password_confirm: String = "",
		organization_id: Int = 1,
		lang_id: Int = 2,
		timezone_id:Int = 2,
		locale_id:Int = 2,
		country_id:Int = 2
    ) extends Form {
}