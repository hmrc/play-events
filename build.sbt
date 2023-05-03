import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "play-events"


val scala2_12 = "2.13.10"

lazy val PlayEvents = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin)
  .settings(
    name := appName,
    majorVersion := 2,
    isPublicArtefact:= true,
    scalaVersion := scala2_12,
    resolvers += "releases" at "https://dl.bintray.com/hmrc/releases/",
    libraryDependencies ++= Seq(
      httpVerbs,
      httpAuditing,
      scalaTest,
      mockito,
    ),
    scalacOptions ++= Seq("-feature", "-language:postfixOps")
  )

val httpVerbs = "uk.gov.hmrc" %% "http-verbs-play-28" % "14.9.0"
val httpAuditing = "uk.gov.hmrc" %% "play-auditing-play-28" % "8.6.0"

val scope = "test"
val scalaTest = "uk.gov.hmrc" %% "bootstrap-test-play-28" % "7.15.0" % scope
val mockito = "org.scalatestplus"  %% "mockito-3-4"  % "3.2.9.0" % scope
