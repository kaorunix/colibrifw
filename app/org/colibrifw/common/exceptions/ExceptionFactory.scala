package org.colibrifw.common.exceptions

trait ExceptionFactory {
	def throwApplication(code:String, message:String) = new ApplicationException(code, message)
	def throwSystem(code:String, message:String) = new SystemException(code, message)
}