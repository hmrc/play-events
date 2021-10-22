import uk.gov.hmrc.SbtArtifactory
import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "play-events"

val scala2_12 = "2.12.13"

lazy val PlayEvents = (project in file("."))
  .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning, SbtArtifactory)
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
      mockito
    )
  )

val httpVerbs = "uk.gov.hmrc" %% "http-verbs-play-28" % "13.10.0"
val httpAuditing = "uk.gov.hmrc" %% "play-auditing-play-28" % "7.10.0"

val scope = "test"
val scalaTest = "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.16.0" % scope
val mockito = "org.scalatestplus"  %% "mockito-3-4"  % "3.2.9.0" % scope

