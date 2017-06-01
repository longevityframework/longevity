package longevity.unit.manual.model.collections.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  title: Option[String],
  firstName: String,
  lastName: String,
  emails: Set[String])
