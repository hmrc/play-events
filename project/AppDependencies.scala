import sbt._

object AppDependencies {
  private val playVersion = "play-30"
  private val bootstrapVersion = "9.5.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"http-verbs-$playVersion" % "14.12.0",
    "uk.gov.hmrc" %% s"play-auditing-$playVersion" % "9.0.0"
  )

  lazy val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc" %% s"bootstrap-test-$playVersion" % bootstrapVersion,
    "org.scalatestplus"  %% "mockito-4-2"  % "3.2.11.0"
  ).map(_ % "test")

  val all: Seq[sbt.ModuleID] = compile ++ test
}
