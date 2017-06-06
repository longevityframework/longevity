package longevity.unit.manual.model.persistents.ex4

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

// end prelude

import longevity.model.PType
import longevity.model.ptype.Prop

case class User(
  username: String,
  firstName: String,
  lastName: String)

object User extends PType[DomainModel, User] {
  object props {
    object username extends Prop[User, String]("username")
    object firstName extends Prop[User, String]("firstName")
    object lastName extends Prop[User, String]("lastName")
  }
}
