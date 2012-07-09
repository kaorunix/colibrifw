package org.colibrifw.common.forms

case class LoginForm(
		account: String,
		password: String
    ) extends Form {
}