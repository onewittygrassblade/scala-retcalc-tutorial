name := "scala-retcalc-tutorial"

version := "0.1"

scalaVersion := "2.13.7"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings
  )

libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.10"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "it,test"
libraryDependencies += "org.typelevel" %% "cats-core" % "2.3.0"
