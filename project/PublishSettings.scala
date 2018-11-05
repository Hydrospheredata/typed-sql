import sbt._
import sbt.Keys._

object PublishSettings {

  val settings = Seq(
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots/")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2/")
    },
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },

    licenses := Seq("Apache 2.0 License" -> url("https://github.com/Hydrospheredata/typed-sql")),
    homepage := Some(url("https://github.com/Hydrospheredata/typed-sql")),
    scmInfo := Some(
          ScmInfo(
            url("https://github.com/Hydrospheredata/typed-sql"),
            "scm:git@github.com:Hydrospheredata/typed-sql.git"
          )
        ),
    developers := List(
        Developer(
          id = "dos65",
          name = "Vadim Chelyshov",
          url = url("https://github.com/dos65"),
          email = "qtankle@gmail.com"
        )
      )
  )
}
