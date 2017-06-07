package longevity.unit.manual.poly.persistents.ex2

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email()
case class Markdown()
case class Uri()
case class Username()

// end prelude

import longevity.model.CType
import longevity.model.DerivedPType
import longevity.model.PolyPType

case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

object UserProfile extends CType[DomainModel, UserProfile]

trait User {
  val username: Username
  val email: Email
}

object User extends PolyPType[DomainModel, User] {
  object props {
    // ...
  }
}

case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member extends DerivedPType[DomainModel, Member, User] {
  object props {
    // ...
  }
}

case class Commenter(
  username: Username,
  email: Email)
extends User

object Commenter extends DerivedPType[DomainModel, Commenter, User] {
  object props {
    // ...
  }
}
