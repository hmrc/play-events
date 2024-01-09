val artefactsUrl: String = "https://open.artefacts.tax.service.gov.uk"

resolvers += "HMRC-open-artefacts-maven" at (artefactsUrl + "/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy", url(artefactsUrl + "/ivy2"))(Resolver.ivyStylePatterns)
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.18.0")
