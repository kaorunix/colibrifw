package org.colibrifw.common.exceptions

class CommonException(var code:String, var mes:String) extends Exception(code.toString() + ":" + mes) {
  /**
   * java向けのエラーコードsetter
   * @param c エラーコード
   */
	def setCode(c:String) = { code = c }
  /**
   * java向けのエラーコードgetter
   * @return code
   */
	def getCode() = code
}