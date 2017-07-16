package longevity.unit.model.pTypeSpec

import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
sealed trait User {
  val username: Username
}

object User {
  implicit val usernameKey = primaryKey(props.username)
}

@derivedPersistent[DomainModel, User]
case class EmailedUser(username: Username, email: Email) extends User

object EmailedUser {
  implicit val emailKey = primaryKey(props.email)
}

