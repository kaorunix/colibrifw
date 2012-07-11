import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "colibriFW"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
	"javax.mail" % "mail" % "1.4.1",
	"mysql" % "mysql-connector-java" % "5.1.8"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here      
    )

}
