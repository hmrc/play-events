import uk.gov.hmrc.SbtArtifactory
import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray

val appName = "play-events"

val scala2_12 = "2.12.11"

lazy val PlayEvents = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
  .settings(
    name := appName,
    majorVersion := 2,
    makePublicallyAvailableOnBintray := true,
    scalaVersion := scala2_12,
    resolvers += "releases" at "https://dl.bintray.com/hmrc/releases/",
    libraryDependencies ++= Seq(
      httpVerbs,
      httpAuditing,
      scalaTest,
      pegdown,
      mockito,
      hamcrest
    )
  )

val httpVerbs = "uk.gov.hmrc" %% "http-verbs-play-26" % "12.0.0"
val httpAuditing = "uk.gov.hmrc" %% "play-auditing-play-26" % "5.8.0"

val scope = "test"
val scalaTest = "org.scalatest" %% "scalatest" % "3.0.8" % scope
val mockito = "org.mockito" % "mockito-all" % "1.10.19" % scope
val pegdown = "org.pegdown" % "pegdown" % "1.6.0" % scope
val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.3" % scope
