package longevity.unit.manual.model.persistents.ex3

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.PType

case class User(
  username: String,
  firstName: String,
  lastName: String)

object User extends PType[DomainModel, User] {
  object props {
    // ...
  }
}

