package longevity.migrations.unit

package poly {

  import longevity.model.annotations._

  @domainModel trait DomainModel

  @polyPersistent[DomainModel]
  trait User {
    val username: Username
    val email: Email
    val fullName: FullName
  }

  object User {
    implicit val usernameKey = primaryKey(props.username)
  }

  @derivedPersistent[DomainModel, User]
  case class Member(
    username: Username,
    email: Email,
    fullName: FullName,
    memberId: Long) extends User

  @derivedPersistent[DomainModel, User]
  case class Commenter(
    username: Username,
    email: Email,
    fullName: FullName) extends User

  @keyVal[DomainModel, User]
  case class Username(username: String)

  @component[DomainModel]
  case class Email(email: String)

  @component[DomainModel]
  case class FullName(
    last: String,
    first: String,
    title: Option[String])

}
