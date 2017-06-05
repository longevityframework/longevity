package longevity.unit.manual.model.keyval.ex3

import longevity.model.annotations.domainModel
import longevity.model.annotations.persistent

@domainModel trait DomainModel

@persistent[DomainModel]
case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])

object User {
  implicit val usernameKey = key(props.username)
}

// end prelude

import longevity.model.KVType

case class Username(username: String)

object Username extends KVType[DomainModel, User, Username]
