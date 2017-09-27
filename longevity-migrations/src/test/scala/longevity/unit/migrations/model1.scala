package model1 {

  import longevity.model.annotations._

  // first, define our domain classes:

  // gather all the domain classes into a domain model
  @domainModel trait DomainModel

  // the @persistent is the thing we want to persist in its own table
  @persistent[DomainModel]
  case class User(
    username: Username,
    email: Email,
    fullName: FullName)

  object User {
    // the primaryKey describes how we want to retrieve the objects
    implicit val usernameKey = primaryKey(props.username)
  }

  // @keyVal is the type of the values we can use to look up by key
  @keyVal[DomainModel, User]
  case class Username(username: String)

  // a @component is a part of the object we want to persist
  @component[DomainModel]
  case class Email(email: String)

  @component[DomainModel]
  case class FullName(
    last: String,
    first: String,
    title: Option[String])

}
