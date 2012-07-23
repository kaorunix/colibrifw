package org.colibrifw.common.models

import anorm._
import java.sql.Date

abstract class Record(
    val id:Pk[Int],
    val create_date:Date,
    val update_date:Date,
    val update_user_id:Int,
    val status_id:Int
    ) extends Model{
  val status = Status(status_id)
  val update_user = User(update_user_id)
}