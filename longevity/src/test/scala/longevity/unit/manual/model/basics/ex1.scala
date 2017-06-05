package longevity.unit.manual.model.basics.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.annotations.persistent
import org.joda.time.DateTime

@persistent[DomainModel]
case class User(
  username: String,
  firstName: String,
  lastName: String,
  dateJoined: DateTime,
  numCats: Int,
  accountSuspended: Boolean = false)
