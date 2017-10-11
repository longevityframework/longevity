package longevity.migrations.integration.basic.m2

@longevity.model.annotations.persistent[M2]
case class User(
  username: Username,
  fullname: Fullname)

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
