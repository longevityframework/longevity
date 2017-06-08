package longevity.unit.manual.repo.poly.ex1

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

import longevity.model.annotations.keyVal
@keyVal[DomainModel, User] case class Username(username: String)
@keyVal[DomainModel, Member] case class Email(email: String)

// end prelude

import longevity.model.annotations.derivedPersistent
import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
trait User {
  val username: Username
}

object User {
  implicit val usernameKey = key(props.username)
}

@derivedPersistent[DomainModel, User]
case class Member(
  username: Username,
  email: Email,
  numCats: Int)
extends User

object Member {
  implicit val emailKey = key(props.email)
}

@derivedPersistent[DomainModel, User]
case class Commenter(
  username: Username)
extends User
