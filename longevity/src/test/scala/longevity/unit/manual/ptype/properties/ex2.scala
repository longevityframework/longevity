package longevity.unit.manual.ptype.properties.ex2

import longevity.model.annotations.domainModel

@domainModel trait DomainModel

import longevity.model.annotations.component

@component[DomainModel] case class Email(email: String)
@component[DomainModel] case class Markdown(markdown: String)
@component[DomainModel] case class Uri(uri: String)

@component[DomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

case class User(
  username: String,
  email: Email,
  profile: UserProfile)

// end prelude

import longevity.model.PType
import longevity.model.ptype.Prop

object User extends PType[DomainModel, User] {
  object props {
    object username extends Prop[User, String]("username")
    object email extends Prop[User, Email]("email")
    object profile extends Prop[User, UserProfile]("profile") {
      object tagline extends Prop[User, String]("tagline")
      object imageUri extends Prop[User, Uri]("imageUri")
      object markdown extends Prop[User, Markdown]("markdown")
    }
  }
}
