import sbt.Keys._
import sbt._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning


object HmrcBuild extends Build {

  import BuildDependencies._
  import uk.gov.hmrc.DefaultBuildSettings._

  val appName = "play-events"

  lazy val PlayEvents = (project in file("."))
    .enablePlugins(SbtAutoBuildPlugin, SbtGitVersioning)
    .settings(
      name := appName,
      scalaVersion := "2.11.7",
      crossScalaVersions := Seq("2.11.7"),
      libraryDependencies ++= Seq(
        Compile.httpVerbs,
        Compile.httpAuditing,
        Test.scalaTest,
        Test.pegdown,
        Test.mockito,
        Test.hamcrest
      ),
      Developers()
    )
}

private object BuildDependencies {

  object Compile {
    val httpVerbs = "uk.gov.hmrc" %% "http-verbs" % "3.0.0" % "provided"
    val httpAuditing = "uk.gov.hmrc" %% "play-auditing" % "0.2.0" % "provided"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.4" % scope
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.5.0" % scope
    val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.3" % scope
  }

  object Test extends Test("test")

}

object Developers {

  def apply() = developers := List[Developer]()
}
