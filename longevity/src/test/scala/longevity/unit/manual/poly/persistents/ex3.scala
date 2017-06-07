package longevity.unit.manual.poly.persistents.ex3

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email()
case class Markdown()
case class Uri()

import longevity.model.annotations.keyVal
@keyVal[DomainModel, User] case class Username()

import longevity.model.annotations.component
@component[DomainModel]
case class UserProfile(
  tagline: String,
  imageUri: Uri,
  description: Markdown)

// end prelude

import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
trait User {
  val username: Username
  val email: Email
}

object User {
  implicit val usernameKey = key(props.username)
  override val indexSet = Set(index(props.email))
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  profile: UserProfile)
extends User

object Member {
  override val indexSet = Set(index(props.profile.tagline))
}
