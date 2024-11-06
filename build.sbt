import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "play-events"

lazy val PlayEvents = (project in file("."))
  .settings(
    name := appName,
    majorVersion := 2,
    targetJvm := "jvm-11",
    scalaVersion := "2.13.12",
    isPublicArtefact := true,
    libraryDependencies ++= AppDependencies.all,
    scalacOptions ++= Seq("-feature", "-language:postfixOps")
  )
