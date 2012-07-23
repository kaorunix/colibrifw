package org.colibrifw.common.utils

import javax.crypto._
import org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import javax.crypto.spec.SecretKeySpec

object Encryption {
//  val kg = KeyGenerator.getInstance("HmacSHA1")
  //val kg = KeyGenerator.//.engineInit("HmacSHA1", 1020)

  val key = new SecretKeySpec("colibri".getBytes,"HmacSHA1")

  val mac = Mac.getInstance("HmacSHA1")
  mac.init(key)

  def encript(word:String):String = {
    val s = mac.doFinal(word.getBytes).map(_ & 0xFF).map(_.toHexString).mkString
    println(s)
    s
  }
}