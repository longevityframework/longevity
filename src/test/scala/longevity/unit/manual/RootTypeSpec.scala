package longevity.unit.manual

import org.scalatest._
import org.scalatest.OptionValues._

object RootTypeSpec {

  // duplicated at https://gist.github.com/sullivan-/e2151a996350786c0e27
  // used in http://longevityframework.github.io/longevity/manual/root-type/properties.html
  object properties1 {
    import longevity.subdomain._

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    val emailShorthand = Shorthand[Email, String]
    val markdownShorthand = Shorthand[Markdown, String]
    val uriShorthand = Shorthand[Uri, String]
    implicit val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    case class User(
      username: String,
      email: Email,
      profile: UserProfile)
    extends Root

    object User extends RootType[User] {

      // fully typed:
      val profileDescription: longevity.subdomain.root.Prop[User, Markdown] =
        prop[Markdown]("profile.description")

      // brief:
      val usernameProp = prop[String]("username")
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User, UserProfile))
  }

  // duplicated at https://gist.github.com/sullivan-/b08a7e729227c8e1abdf
  // used in http://longevityframework.github.io/longevity/manual/root-type/properties.html
  object properties2 {

    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      email: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[String]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
        val email = prop[String]("email")
      }
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }


  // duplicated at https://gist.github.com/sullivan-/e2ef663857157a03a301
  // used in http://longevityframework.github.io/longevity/manual/root-type/keys.html
  object keys1 {
    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[String]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
      }
      val usernameKey = key(props.username)
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/b72900a6882b557e6728
  // used in http://longevityframework.github.io/longevity/manual/root-type/keys.html
  object keys2 {
    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[String]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
      }
      val usernameKey = key(props.username)
      val fullnameKey = key(props.firstName, props.lastName)
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/eaa0f96308d6f16a36c3
  // used in http://longevityframework.github.io/longevity/manual/root-type/indexes.html
  object indexes1 {
    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[String]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
      }
      val usernameKey = key(props.username)
      val lastFirstIndex = index(props.lastName, props.firstName)
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

}

/** exercises code samples found in the root type section of the user manual. the samples themselves are
 * in [[RootTypeSpec]] companion object. we include them in the tests here to force the initialization of the
 * subdomains, and to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/root-type
 */
class RootTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  import RootTypeSpec._
  import emblem.imports._
  // import longevity.subdomain._

  "user manual example code" should "produce correct subdomains" in {

    properties1.subdomain.name should equal ("blogging")
    properties2.subdomain.name should equal ("blogging")

    {
      keys1.subdomain.name should equal ("blogging")
      keys1.subdomain.entityTypePool.size should equal (1)
      keys1.subdomain.entityTypePool.values.head should equal (keys1.User)
      keys1.subdomain.rootTypePool.size should equal (1)
      keys1.subdomain.rootTypePool.values.head should equal (keys1.User)
      keys1.User.keySet.size should equal (1)
      keys1.User.keySet.head should equal (keys1.User.usernameKey)
      keys1.User.usernameKey.props.size should equal (1)
      val prop = keys1.User.usernameKey.props.head
      prop.path should equal ("username")
      prop.typeKey should equal (typeKey[String])
    }

    {
      keys2.subdomain.name should equal ("blogging")
      keys2.subdomain.entityTypePool.size should equal (1)
      keys2.subdomain.entityTypePool.values.head should equal (keys2.User)
      keys2.subdomain.rootTypePool.size should equal (1)
      keys2.subdomain.rootTypePool.values.head should equal (keys2.User)
      keys2.User.keySet.size should equal (2)
      keys2.User.keySet.find(_.props.size == 1).value should equal (keys2.User.usernameKey)
      keys2.User.usernameKey.props.size should equal (1)
      val usernameProp = keys2.User.usernameKey.props.head
      usernameProp.path should equal ("username")
      usernameProp.typeKey should equal (typeKey[String])

      keys2.User.keySet.find(_.props.size == 2).value should equal (keys2.User.fullnameKey)
      keys2.User.fullnameKey.props.size should equal (2)
      val firstNameProp = keys2.User.fullnameKey.props.find(_.path == "firstName").value
      firstNameProp.path should equal ("firstName")
      firstNameProp.typeKey should equal (typeKey[String])
      val lastNameProp = keys2.User.fullnameKey.props.find(_.path == "lastName").value
      lastNameProp.path should equal ("lastName")
      lastNameProp.typeKey should equal (typeKey[String])
    }

    indexes1.subdomain.name should equal ("blogging")

  }

}
