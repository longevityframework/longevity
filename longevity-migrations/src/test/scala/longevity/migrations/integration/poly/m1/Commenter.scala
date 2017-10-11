package longevity.migrations.integration.poly.m1

@longevity.model.annotations.derivedPersistent[M1, User]
case class Commenter(
  username: Username,
  last: String,
  first: String,
  title: Option[String]) extends User
