package longevity.unit.manual.model.keyval.ex3

import longevity.model.annotations.domainModel
import longevity.model.annotations.persistent

@domainModel trait DomainModel

@persistent[DomainModel](keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])

// end prelude

import longevity.model.KVType

case class Username(username: String)

object Username extends KVType[DomainModel, User, Username]
