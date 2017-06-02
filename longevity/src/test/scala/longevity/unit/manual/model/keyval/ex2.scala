package longevity.unit.manual.model.keyval.ex2

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@persistent[DomainModel](keySet = Set(key(props.username)))
case class User(
  username: Username,
  firstName: String,
  lastName: String,
  sponsor: Option[Username])
