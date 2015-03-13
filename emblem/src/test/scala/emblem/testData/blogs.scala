package emblem.testData

import emblem._

/** for type map happy cases */
object blogs {


  implicit def stringToEmail(email: String): Email = Email(email)

  implicit def stringToMarkdown(markdown: String): Markdown = Markdown(markdown)

  implicit def stringToUri(uri: String): Uri = Uri(uri)

  implicit def intToZipcode(zip: Int): Zipcode = Zipcode(zip)

  object CrmUser {

    private val dummyAddress = CrmAddress("street1", "street2", "city", "state", /*0*/1210)
    private val dummyUser = CrmUser(Uri("uri"), "firstName", "lastName", dummyAddress)

    def apply(uri: Uri): CrmUser = dummyUser.copy(uri = uri)
  }

  // entities

  trait CrmEntity extends HasEmblem

  case class CrmUser(
    uri: Uri,
    firstName: String,
    lastName: String,
    address: CrmAddress) extends CrmEntity
  val userEmblem = emblemFor[CrmUser]

  case class CrmAddress(
    street1: String,
    street2: String,
    city: String,
    state: String,
    zipcode: Zipcode) extends CrmEntity
  val addressEmblem = emblemFor[CrmAddress]

  case class CrmBlog(uri: Uri) extends CrmEntity
  val blogEmblem = emblemFor[CrmBlog]

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

  trait CrmEntityType[E <: CrmEntity]
  object userType extends CrmEntityType[CrmUser]
  object blogType extends CrmEntityType[CrmBlog]

  // repos

  trait CrmRepo[E <: CrmEntity] {
    var saveCount = 0
    def save(entity: E): Unit = saveCount += 1
  }
  class CrmUserRepo extends CrmRepo[CrmUser]
  class CrmBlogRepo extends CrmRepo[CrmBlog]

}
