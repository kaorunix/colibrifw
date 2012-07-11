package org.colibrifw.common.utils

import java.util.Properties
import javax.mail.{Session, Store}

class PopMail(
    val popHost:String = "",
    val account:String = "",
    val password:String = ""
    ) {
  def popMail= {
    val store = Session.getDefaultInstance(new Properties, null).getStore("pop3")
    store.connect(popHost, account, password)
    store.close
  }
}