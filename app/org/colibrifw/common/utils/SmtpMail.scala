package org.colibrifw.common.utils

import java.util.Properties
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.internet.InternetAddress
import javax.mail.Message
import java.util.Date
import javax.mail.Address

class SmtpMail(
    val account:String = "",
    val password:String = "",
    val fromAddress:String = "",
    val smtpHost:String = "",
    val charset:String = "UTF-8"
    ) {
  val props = new Properties
  props.put("mail.smtp.host", smtpHost)
  // SMTP認証
  props.setProperty("mail.smtp.auth", "true")
  val session = Session.getDefaultInstance(props, null)

  def sendMail(toAddress:String, subject:String, body:String, header:Map[String, String])= {
	val msg = new MimeMessage(session)
	// 送信先の設定
	msg.setRecipient(Message.RecipientType.TO,
	    new InternetAddress(toAddress)
	)
	// 送信元の設定
	msg.setFrom(new InternetAddress(fromAddress))
	// 送信日付の設定
	msg.setSentDate(new Date)
	// ヘッダの設定
	header.map(a => msg.setHeader(a._1, a._2))
	// Subjectの設定
	msg.setSubject(subject, charset)
	// 本文
	msg.setText(body, charset)
	// メール送信
	val tp = session.getTransport("smtp")
	tp.connect(smtpHost, account, password)
	tp.sendMessage(msg,
	    Array[Address](new InternetAddress(toAddress))
	)
  }
}