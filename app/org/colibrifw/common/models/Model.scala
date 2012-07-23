package org.colibrifw.common.models

import anorm.Pk
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

abstract class Model {
}
/*
trait Dao[T <: Model] {
  def simple:anorm.RowParser[T]

  def all[T]():List[T] = {
	DB.withConnection { implicit c =>
	  SQL("SELECT * FROM "+this.getClass().getName+" order by id").as(this.simple.*)
	}
  }
}*/