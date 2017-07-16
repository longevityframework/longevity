package longevity.unit.manual.poly.persistents.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email()
case class Markdown()
case class Uri()
case class Username()

// end prelude

import longevity.model.annotations.component
import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@component[DomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

@polyPersistent[DomainModel]
sealed trait User {
  val username: Username
  val email: Email
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

@derivedPersistent[DomainModel, User]
case class Commenter(
  username: Username,
  email: Email)
extends User
