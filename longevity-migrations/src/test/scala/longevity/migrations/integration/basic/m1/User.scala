package longevity.migrations.integration.basic.m1

@longevity.model.annotations.persistent[M1]
case class User(
  username: Username,
  last: String,
  first: String,
  title: Option[String])

@longevity.model.annotations.keyVal[M1, User]
case class Username(value: String)

object User {
  implicit val usernameKey = primaryKey(props.username)
}
