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
  val userEmblem = Emblem[CrmUser]

  case class CrmAddress(
    street1: String,
    street2: String,
    city: String,
    state: String,
    zipcode: Zipcode) extends CrmEntity
  val addressEmblem = Emblem[CrmAddress]

  case class CrmBlog(uri: Uri) extends CrmEntity
  val blogEmblem = Emblem[CrmBlog]

  val emblemPool = EmblemPool(userEmblem, addressEmblem, blogEmblem)

  // extractors

  case class Email(email: String)
  lazy val emailExtractor = Extractor[String, Email]

  case class Markdown(markdown: String)
  lazy val markdownExtractor = Extractor[String, Markdown]

  case class Uri(uri: String)
  lazy val uriExtractor = Extractor[String, Uri]

  case class Zipcode(zipcode: Int)
  lazy val zipcodeExtractor = Extractor[Int, Zipcode]

  val extractorPool = ExtractorPool(emailExtractor, markdownExtractor, uriExtractor, zipcodeExtractor)

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
