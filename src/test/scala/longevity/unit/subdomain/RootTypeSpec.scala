package longevity.unit.subdomain

import longevity.exceptions.subdomain.root.UnsupportedPropTypeException
import longevity.exceptions.subdomain.root.PropTypeException
import org.scalatest._

object RootTypeSpec {

  import longevity.subdomain._

  case class Email(email: String)
  case class Markdown(markdown: String)
  case class Uri(uri: String)

  implicit def toEmail(email: String) = Email(email)
  implicit def toMarkdown(markdown: String) = Markdown(markdown)
  implicit def toUri(uri: String) = Uri(uri)

  implicit val shorthandPool = ShorthandPool(
    Shorthand[Email, String],
    Shorthand[Markdown, String],
    Shorthand[Uri, String])

  case class User(
    username: String,
    fullname: String,
    email: Email,
    profile: Option[UserProfile] = None)
  extends Root

  object User extends RootType[User] {
    val usernameProp = prop[String]("username")
    val emailProp = prop[Email]("email")
    val usernameKey = key(usernameProp)
    val emailKey = key(emailProp)
  }

  case class UserProfile(
    tagline: String,
    imageUri: Uri,
    description: Markdown)
  extends Entity

  object UserProfile extends EntityType[UserProfile]

  case class Blog(
    uri: Uri,
    title: String,
    description: Markdown,
    authors: Set[Assoc[User]])
  extends Root

  object Blog extends RootType[Blog] {
    val uriProp = prop[Uri]("uri")
    val natKey = key(uriProp)
  }

  case class BlogPost(
    uriPathSuffix: String,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: Assoc[Blog],
    authors: Set[Assoc[User]])
  extends Root

  object BlogPost extends RootType[BlogPost] {
    val blogProp = prop[Assoc[Blog]]("blog")
    val suffixProp = prop[String]("uriPathSuffix")
    val natKey = key(blogProp, suffixProp)
  }

  object BlogCore extends Subdomain("blogging", EntityTypePool(User, UserProfile, Blog, BlogPost))

}

/** test bed for [[RootType]] functionality.
 *
 * this test class was established for the purpose of testing method
 * `RootType.validateQuery`. various flavors of `RootType` methods `keys`,
 * `indexes`, `prop`, `key`, and `index`, are more or less tested via
 * RootType/LongevityContext creation, and these should not need explicit
 * tests. (although it would be worth checking how well these methods are
 * currently covered by unit tests.)
 */
class RootTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  import RootTypeSpec._
  import longevity.subdomain.root._

  behavior of "RootType.validateQuery"

  it should "leave static queries as-is" in {
    val usernameVal = "usernameVal"
    val emailVal: Email = "emailVal"

    val squery = Query.or(
      Query.eqs(User.usernameProp, usernameVal),
      Query.eqs(User.emailProp, emailVal))

    User.validateQuery(squery) should equal (squery)
  }

}
