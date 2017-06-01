package longevity.unit.manual.model.persistents.ex2

import longevity.model.annotations.domainModel
import longevity.model.annotations.persistent

@domainModel trait DomainModel

@persistent[DomainModel](keySet = emptyKeySet)
case class User(
  username: String,
  firstName: String,
  lastName: String)
