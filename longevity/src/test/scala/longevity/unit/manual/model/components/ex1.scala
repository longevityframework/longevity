package longevity.unit.manual.model.components.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.component
import longevity.model.annotations.persistent

@component[DomainModel]
case class FullName(
  firstName: String,
  lastName: String)

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  fullName: FullName)
