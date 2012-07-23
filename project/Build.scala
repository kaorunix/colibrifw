import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "colibriFW"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
	"com.github.mumoshu" %% "play2-memcached" % "0.2.1-SNAPSHOT",
	"jp.t2v" %% "play20.auth" % "0.2",
	"javax.mail" % "mail" % "1.4.1",
	"mysql" % "mysql-connector-java" % "5.1.8"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
	resolvers += "Sonatype OSS Snapshots Repository" at "http://oss.sonatype.org/content/groups/public",
    	resolvers += "Spy Repository" at "http://files.couchbase.com/maven2", // required to resolve `spymemcached`, the plugin's dependency.
	resolvers += "t2v.jp repo" at "http://www.t2v.jp/maven-repo/"
      // Add your own project settings here      
    )

}
