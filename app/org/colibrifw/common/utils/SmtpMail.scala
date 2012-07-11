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
  // SMTP�F��
  props.setProperty("mail.smtp.auth", "true")
  val session = Session.getDefaultInstance(props, null)

  def sendMail(toAddress:String, subject:String, body:String, header:Map[String, String])= {
	val msg = new MimeMessage(session)
	// ���M��̐ݒ�
	msg.setRecipient(Message.RecipientType.TO,
	    new InternetAddress(toAddress)
	)
	// ���M���̐ݒ�
	msg.setFrom(new InternetAddress(fromAddress))
	// ���M���t�̐ݒ�
	msg.setSentDate(new Date)
	// �w�b�_�̐ݒ�
	header.map(a => msg.setHeader(a._1, a._2))
	// Subject�̐ݒ�
	msg.setSubject(subject, charset)
	// �{��
	msg.setText(body, charset)
	// ���[�����M
	val tp = session.getTransport("smtp")
	tp.connect(smtpHost, account, password)
	tp.sendMessage(msg,
	    Array[Address](new InternetAddress(toAddress))
	)
  }
}