package longevity.unit.manual.ptype.keys.ex2

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@keyVal[DomainModel, User]
case class FullName(last: String, first: String)

@persistent[DomainModel]
case class User(
  username: Username,
  fullName: FullName)

object User {
  implicit val usernameKey = key(props.username)
  implicit val fullNameKey = key(props.fullName)
}
