scalaVersion := "2.12.7"

organization := "io.hydrosphere"
name := "typed-sql"
version := "0.0.1"

sourceGenerators in Compile += (sourceManaged in Compile).map(dir => Boilerplate.gen(dir)).taskValue

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.tpolecat" %% "doobie-core"      % "0.6.0",
  "org.tpolecat" %% "doobie-h2"        % "0.6.0",          // H2 driver 1.4.197 + type mappings.
  "org.tpolecat" %% "doobie-hikari"    % "0.6.0",          // HikariCP transactor.
  "org.tpolecat" %% "doobie-postgres"  % "0.6.0",          // Postgres driver 42.2.5 + type mappings.

  "org.scalatest" %% "scalatest" %  "3.0.3" % "test"
)
