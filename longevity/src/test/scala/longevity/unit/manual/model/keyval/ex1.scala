package longevity.unit.manual.model.keyval.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@persistent[DomainModel]
case class User(
  username: Username,
  firstName: String,
  lastName: String)

object User {
  implicit lazy val usernameKey = key(props.username)
}
