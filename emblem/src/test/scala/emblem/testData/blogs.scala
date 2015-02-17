package emblem.testData

import emblem._

/** for type map happy cases */
object blogs {


  implicit def stringToEmail(email: String): Email = Email(email)

  implicit def stringToMarkdown(markdown: String): Markdown = Markdown(markdown)

  implicit def stringToUri(uri: String): Uri = Uri(uri)

  implicit def intToZipcode(zip: Int): Zipcode = Zipcode(zip)

  object User {

    private val dummyAddress = Address("street1", "street2", "city", "state", /*0*/1210)
    private val dummyUser = User(Uri("uri"), "firstName", "lastName", dummyAddress)

    def apply(uri: Uri): User = dummyUser.copy(uri = uri)
  }

  // entities

  trait Entity extends HasEmblem

  case class User(
    uri: Uri,
    firstName: String,
    lastName: String,
    address: Address) extends Entity
  val userEmblem = emblemFor[User]

  case class Address(
    street1: String,
    street2: String,
    city: String,
    state: String,
    zipcode: Zipcode) extends Entity
  val addressEmblem = emblemFor[Address]

  case class Blog(uri: Uri) extends Entity
  val blogEmblem = emblemFor[Blog]

  val emblemPool = EmblemPool(userEmblem, addressEmblem, blogEmblem)

  // shorthands

  case class Email(email: String)
  lazy val emailShorthand = shorthandFor[Email, String]

  case class Markdown(markdown: String)
  lazy val markdownShorthand = shorthandFor[Markdown, String]

  case class Uri(uri: String)
  lazy val uriShorthand = shorthandFor[Uri, String]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeShorthand = shorthandFor[Zipcode, Int]

  val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand, zipcodeShorthand)

  // entity types

  trait EntityType[E <: Entity]
  object userType extends EntityType[User]
  object blogType extends EntityType[Blog]

  // repos

  trait Repo[E <: Entity] {
    var saveCount = 0
    def save(entity: E): Unit = saveCount += 1
  }
  class UserRepo extends Repo[User]
  class BlogRepo extends Repo[Blog]

}
