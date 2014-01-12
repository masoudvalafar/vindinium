import sbt._
import Keys._

object ApplicationBuild extends Build {
  val pom = (baseDirectory in Compile) { base ⇒
    import scala.xml.XML
    val pomFile = base / "pom.xml"
    XML.loadFile(pomFile)
  }

  // Load project settings from Maven POM at project basedir
  val pomSettings = Project.defaultSettings ++ Seq(
    organization <<= pom { _ \\ "project" \ "groupId" text },
    name <<= pom { _ \\ "project" \ "artifactId" text },
    version <<= pom { _ \\ "project" \ "version" text },
    scalaVersion <<= pom {
      _ \\ "project" \\ "properties" \ "scalaVersion" text
    },
    scalacOptions ++= Seq("-feature", "-deprecation"),
    resolvers <<= pom {
      _ \\ "project" \ "repositories" \ "repository" map (r ⇒
        (r \ "name").text at (r \ "url").text)
    },
    externalResolvers += Resolver.mavenLocal
  ) ++ externalPom( /*dependencies*/ )

  lazy val webconnector = Project(
    id = "vindinium-starter-java", base = file("."),
    settings = pomSettings)
}
