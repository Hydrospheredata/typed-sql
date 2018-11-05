val root = project.in(file("."))
  .settings(PublishSettings.settings: _*)
  .settings(
    scalaVersion := "2.12.7",
    crossScalaVersions := Seq("2.11.12", scalaVersion.value),
    organization := "io.hydrosphere",
    name := "typed-sql",
    version := "0.1.0",
    sourceGenerators in Compile += (sourceManaged in Compile).map(dir => Boilerplate.gen(dir)).taskValue,
    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.3",
      "org.tpolecat" %% "doobie-core"      % "0.6.0",

      "org.tpolecat" %% "doobie-h2"        % "0.6.0" % "test",
      "org.tpolecat" %% "doobie-hikari"    % "0.6.0" % "test",
      "org.tpolecat" %% "doobie-postgres"  % "0.6.0" % "test",
      "org.scalatest"%% "scalatest"        % "3.0.3" % "test",
      "org.tpolecat" %% "doobie-scalatest" % "0.6.0" % "test"
    )
  )
  
