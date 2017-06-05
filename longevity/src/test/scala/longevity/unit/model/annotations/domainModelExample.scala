package longevity.unit.model.annotations.domainModelExample

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent
import longevity.model.annotations.{ domainModel => dm }

@dm trait DomainModel

@keyVal[DomainModel, User] case class Username(username: String)

@persistent[DomainModel]
case class User(
  username: Username,
  firstName: String,
  lastName: String)

object User {
  implicit lazy val usernameKey = key(props.username)
}
