import sbt.Keys._
import sbt.{Def, _}
import uk.gov.hmrc.{SbtArtifactory, SbtAutoBuildPlugin}
import sbt.Keys._
import uk.gov.hmrc.SbtAutoBuildPlugin
import uk.gov.hmrc.versioning.SbtGitVersioning
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion
import uk.gov.hmrc.SbtArtifactory.autoImport.makePublicallyAvailableOnBintray


object HmrcBuild extends Build {

  import BuildDependencies._
  import uk.gov.hmrc.DefaultBuildSettings._

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
    val httpVerbs = "uk.gov.hmrc" %% "http-core" % "0.6.0" % "provided"
    val httpAuditing = "uk.gov.hmrc" %% "play-auditing" % "3.2.1" % "provided"
  }

  sealed abstract class Test(scope: String) {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % scope
    val mockito = "org.mockito" % "mockito-all" % "1.9.5" % scope
    val pegdown = "org.pegdown" % "pegdown" % "1.5.0" % scope
    val hamcrest = "org.hamcrest" % "hamcrest-all" % "1.3" % scope
  }

  object Test extends Test("test")

}

object Developers {

  def apply(): Def.Setting[List[Developer]] = developers := List[Developer]()
}
