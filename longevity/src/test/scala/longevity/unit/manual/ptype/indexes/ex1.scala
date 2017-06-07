package longevity.unit.manual.ptype.indexes.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.component
import longevity.model.annotations.keyVal
import longevity.model.annotations.persistent

@keyVal[DomainModel, User]
case class Username(username: String)

@component[DomainModel]
case class FullName(last: String, first: String)

@persistent[DomainModel]
case class User(
  username: Username,
  fullName: FullName)

object User {
  implicit val usernameKey = key(props.username)
  override val indexSet = Set(
    index(props.fullName.last, props.fullName.first))
}
