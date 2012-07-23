package org.colibrifw.common.models

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._

class LangTest extends Specification {
  "Lang Table" should {
    "have record whose id is 1" in {
      val lang = Lang(1)
      lang.get.acronym must equalTo(Some("ja"))
      lang.get.description must equalTo(None)
      lang.get.name must equalTo(Some("Japanese"))
    }
  }

}