package simple.v1 {
  import longevity.model.annotations._

  @domainModel trait DomainModel

  @persistent[DomainModel]
  case class User(
    username: Username,
    email: Email,
    last: String,
    first: String,
    title: Option[String])

  object User {
    implicit val usernameKey = primaryKey(props.username)
  }

  @keyVal[DomainModel, User]
  case class Username(username: String)

  @component[DomainModel]
  case class Email(email: String)
}
