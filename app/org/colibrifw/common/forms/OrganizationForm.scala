package org.colibrifw.common.forms

import org.colibrifw.common.exceptions.NotFoundException
import org.colibrifw.common.models.Organization

case class OrganizationCreateForm(
		name:String,
		description:Option[String]
    ) extends Form {
}

case class OrganizationModifyForm(
        organization:OrganizationIdForm,
		description:Option[String]
    ) extends Form {
}

object OrganizationModifyForm {
  def getOrganizationModifyFormById(id:Int) = {
    Organization(id) match {
      case Some(organization:Organization) => OrganizationModifyForm(
          OrganizationIdForm(organization.id.get, organization.name),
          organization.description
          )
      case _ => throw new NotFoundException("10001", format("Not Found Organization by id=%d", id))
    }
  }
}


case class OrganizationIdForm(
    id:Int,
    name:String
) extends Form {}
