package emblem.testData

import emblem._

/** for type map happy cases */
object blogs {

  // entities

  trait Entity extends HasEmblem

  case class Blog(uri: String) extends Entity
  val blogEmblem = emblemFor[Blog]

  case class User(uri: String) extends Entity
  val userEmblem = emblemFor[User]

  val emblemPool = EmblemPool(blogEmblem, userEmblem)

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
