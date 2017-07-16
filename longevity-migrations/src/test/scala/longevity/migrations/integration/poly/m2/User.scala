package longevity.migrations.integration.poly.m2

@longevity.model.annotations.polyPersistent[M2]
sealed trait User {
  val username: Username
  val fullname: Fullname
}

object User {
  implicit val usernameKey = primaryKey(props.username)
}

@longevity.model.annotations.keyVal[M2, User]
case class Username(value: String)

@longevity.model.annotations.component[M2]
case class Fullname(
  last: String,
  first: String,
  title: Option[String])

@longevity.model.annotations.derivedPersistent[M2, User]
case class Member(
  username: Username,
  fullname: Fullname,
  memberId: Long) extends User

@longevity.model.annotations.derivedPersistent[M2, User]
case class Commenter(
  username: Username,
  fullname: Fullname) extends User
