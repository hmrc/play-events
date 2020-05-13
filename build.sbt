import uk.gov.hmrc.SbtArtifactory
import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray

val appName = "play-events"

lazy val PlayEvents = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(majorVersion := 2)
  .settings(makePublicallyAvailableOnBintray := true)
  .settings(
    name := appName,
    scalaVersion := "2.11.12",
    crossScalaVersions := Seq("2.11.12"),
    libraryDependencies ++= Seq(
      httpVerbs,
      httpAuditing,
      scalaTest,
      pegdown,
      mockito,
      hamcrest
    ),
    developers := List[Developer]()
  )

val httpVerbs = "uk.gov.hmrc" %% "http-core" % "2.4.0" % "provided"
val httpAuditing = "uk.gov.hmrc" %% "play-auditing" % "4.3.0-play-26" % "provided"

val scope = "test"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % scope
val mockito = "org.mockito" % "mockito-all" % "1.10.19" % scope
val pegdown = "org.pegdown" % "pegdown" % "1.6.0" % scope
val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.3" % scope
