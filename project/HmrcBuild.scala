import sbt.Keys._
import sbt._
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.{SbtAutoBuildPlugin, ShellPrompt}

import scala.util.Properties._


object HmrcBuild extends Build {

  import BuildDependencies._
  import uk.gov.hmrc.DefaultBuildSettings._

  val appName = "play-events"
  val appVersion = envOrElse("PLAY_EVENTS_VERSION", "999-SNAPSHOT")

  lazy val PlayEvents = (project in file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      name := appName,
      version := appVersion,
      targetJvm := "jvm-1.7",
      shellPrompt := ShellPrompt(appVersion),
      crossScalaVersions := Seq("2.11.2", "2.10.4"),
      libraryDependencies ++= Seq(
        Compile.httpVerbs,
        Compile.playFrontend,
        Test.scalaTest,
        Test.pegdown,
        Test.mockito
      ),
      Developers()
    )
}
private object BuildDependencies {

  object Compile {
    val httpVerbs = "uk.gov.hmrc" %% "http-verbs" % "1.8.0" % "provided"
    val playFrontend = "uk.gov.hmrc" %% "play-frontend" % "17.0.0" % "provided"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % scope
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.5.0" % scope
  }

  object Test extends Test("test")

}

object Developers {

  def apply() = developers := List[Developer]()
}
