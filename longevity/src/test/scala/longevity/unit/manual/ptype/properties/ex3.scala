package longevity.unit.manual.ptype.properties.ex3

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

object User extends PType[DomainModel, User] {
  object props {
    val username = prop[String]("username")
    val email = prop[Email]("email")
    // ...
  }
}
