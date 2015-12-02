package longevity.unit.subdomain

import longevity.exceptions.subdomain.root.UnsupportedPropTypeException
import longevity.exceptions.subdomain.root.PropTypeException
import org.scalatest._

object RootEntityTypeSpec {

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
  extends RootEntity

  object User extends RootEntityType[User] {
    val usernameKey = key("username")
    val emailKey = key("email")
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
  extends RootEntity

  object Blog extends RootEntityType[Blog] {
    val natKey = key("uri")
  }

  case class BlogPost(
    uriPathSuffix: String,
    title: String,
    slug: Option[Markdown] = None,
    content: Markdown,
    labels: Set[String] = Set(),
    blog: Assoc[Blog],
    authors: Set[Assoc[User]])
  extends RootEntity

  object BlogPost extends RootEntityType[BlogPost] {
    val natKey = key("blog", "uriPathSuffix")
  }

  object BlogCore extends Subdomain("blogging", EntityTypePool(User, UserProfile, Blog, BlogPost))

}

/** test bed for [[RootEntityType]] functionality.
 *
 * this test class was established for the purpose of testing method `RootEntityType.validateQuery`.
 * various flavors of `RootEntityType` methods `keys`, `indexes`, `prop`, `key`, and `index`, are more or less
 * tested via RootEntityType/LongevityContext creation, and these should not need explicit tests. (although it
 * would be worth checking how well these methods are currently covered by unit tests.)
 */
class RootEntityTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  import RootEntityTypeSpec._
  import longevity.subdomain.root._

  behavior of "RootEntityType.validateQuery"

  it should "convert simple dynamic queries into static queries" in {
    val usernameVal = "usernameVal"
    val emailVal: Email = "emailVal"

    val squery = Query.or(
      Query.eqs(User.prop[String]("username"), usernameVal),
      Query.eqs(User.prop[Email]("email"), emailVal))

    User.validateQuery(squery) should equal (squery)

    val dquery = Query.or[User](
      Query.eqs("username", usernameVal),
      Query.eqs("email", emailVal))

    dquery should not equal (squery)
    User.validateQuery(dquery) should equal (squery)
  }

  it should "throw exception when the dynamic query is not valid" in {
    val usernameVal = "usernameVal"
    val emailVal: Email = "emailVal"

    intercept[PropTypeException] {
      val dquery = Query.or[User](
        Query.eqs("username", emailVal), // oops! user error
        Query.eqs("email", emailVal))
      User.validateQuery(dquery)
    }

    intercept[PropTypeException] {
      val dquery = Query.or[User](
        Query.eqs("username", usernameVal),
        Query.eqs("email", usernameVal)) // oops! user error
      User.validateQuery(dquery)
    }

  }

  it should "balk on any collection properties, including option properties" in {
    intercept[UnsupportedPropTypeException[_, _]] {
      User.validateQuery(Query.eqs("profile.title", "title"))
    }
  }

}
