addSbtPlugin("com.jsuereth"     % "sbt-pgp"       % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site"      % "1.2.0")
addSbtPlugin("org.scoverage"    % "sbt-scoverage" % "1.5.0")

resolvers += Resolver.url(
  "scoverage-bintray",
  url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(
  Resolver.ivyStylePatterns)
