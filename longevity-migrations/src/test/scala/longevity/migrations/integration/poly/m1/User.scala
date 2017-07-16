package longevity.migrations.integration.poly.m1

@longevity.model.annotations.polyPersistent[M1]
sealed trait User {
  val username: Username
  val last: String
  val first: String
  val title: Option[String]
}

@longevity.model.annotations.keyVal[M1, User]
case class Username(value: String)

object User {
  implicit val usernameKey = primaryKey(props.username)
}

@longevity.model.annotations.derivedPersistent[M1, User]
case class Member(
  username: Username,
  last: String,
  first: String,
  title: Option[String],
  memberId: Long) extends User

@longevity.model.annotations.derivedPersistent[M1, User]
case class Commenter(
  username: Username,
  last: String,
  first: String,
  title: Option[String]) extends User
