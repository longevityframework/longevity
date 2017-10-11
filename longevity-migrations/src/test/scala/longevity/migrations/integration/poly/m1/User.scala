package longevity.migrations.integration.poly.m1

@longevity.model.annotations.polyPersistent[M1]
trait User {
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
