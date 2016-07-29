package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** code samples found in the persistent types section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/ptype
 */
object PTypeSpec {

  // used in http://longevityframework.github.io/longevity/manual/ptype/properties.html
  object properties {

    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType

    case class Email(email: String) extends ValueObject
    object Email extends ValueType[Email]

    case class Markdown(markdown: String) extends ValueObject
    object Markdown extends ValueType[Markdown]

    case class Uri(uri: String) extends ValueObject
    object Uri extends ValueType[Uri]

    import longevity.subdomain.embeddable.Entity
    import longevity.subdomain.embeddable.EntityType
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

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
      object props {

       // fully typed:
       val profileDescription: longevity.subdomain.ptype.Prop[User, Markdown] =
         prop[Markdown]("profile.description")

       // brief:
       val usernameProp = prop[String]("username")
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(Email, Markdown, Uri, UserProfile))

  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  object keys1 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    case class User(
      username: Username,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[Username]("username")
      }
      object keys {
        val username = key(props.username)  
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  object keys2 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    case class FullName(first: String, last: String)
    extends KeyVal[User, FullName](User.keys.fullName)

    case class User(
      username: Username,
      fullName: FullName)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[Username]("username")
        val fullName = prop[FullName]("fullName")
      }
      object keys {
        val username = key(props.username)
        val fullName = key(props.fullName)
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/indexes.html
  object indexes1 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    case class User(
      username: Username,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      object props {
        val username = prop[Username]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
      }
      object keys {
        val username = key(props.username)
      }
      object indexes {
        val fullname = index(props.lastName, props.firstName)
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/root-type/key-sets-and-index-sets.html
  object sets1 {

    import longevity.subdomain.persistent.Root

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    import longevity.subdomain.ptype.Index
    import longevity.subdomain.ptype.AnyKey
    import longevity.subdomain.ptype.Prop
    import longevity.subdomain.ptype.RootType

    object User extends RootType[User] {
      override lazy val propSet = Set.empty[Prop[User, _]]
      override lazy val keySet = Set.empty[AnyKey[User]]
      override lazy val indexSet = Set.empty[Index[User]]
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/root-type/key-sets-and-index-sets.html
  object sets2 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.AnyKey
    import longevity.subdomain.ptype.Prop
    import longevity.subdomain.ptype.RootType

    case class Username(username: String)
    extends KeyVal[User, Username](User.usernameKey)

    case class Email(email: String)
    extends KeyVal[User, Email](User.emailKey)

    case class User(
      username: Username,
      email: Email,
      firstName: String,
      lastName: String)
    extends Root

    object User extends RootType[User] {
      val usernameProp = prop[Username]("username")
      val emailProp = prop[Email]("email")
      val firstNameProp = prop[String]("firstName")
      val lastNameProp = prop[String]("lastName")

      val usernameKey = key(usernameProp)
      val emailKey = key(emailProp)
      val fullnameIndex = index(lastNameProp, firstNameProp)

      override lazy val propSet = Set[Prop[User, _]](usernameProp, emailProp, firstNameProp, lastNameProp)
      override lazy val keySet = Set[AnyKey[User]](usernameKey, emailKey)
      override lazy val indexSet = Set(fullnameIndex)
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

}

/** exercises code samples found in the root type section of the user manual.
 * the samples themselves are in [[PTypeSpec]] companion object. we include
 * them in the tests here to force the initialization of the subdomains, and to
 * perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/root-type
 */
class PTypeSpec extends FlatSpec with GivenWhenThen with Matchers {

  import PTypeSpec._

  "user manual example code" should "produce correct subdomains" in {

    {
      properties.subdomain.name should equal ("blogging")
      properties.subdomain.pTypePool.size should equal (1)
      properties.subdomain.pTypePool.values.head should equal (properties.User)
      properties.subdomain.eTypePool.size should equal (4)
      properties.User.keySet should be ('empty)
    }

    {
      keys1.subdomain.name should equal ("blogging")
      keys1.subdomain.pTypePool.size should equal (1)
      keys1.subdomain.pTypePool.values.head should equal (keys1.User)
      keys1.subdomain.eTypePool.size should equal (0)
      keys1.User.keySet.size should equal (1)
      keys1.User.keySet.head should equal (keys1.User.keys.username)
    }

    {
      keys2.subdomain.name should equal ("blogging")
      keys2.subdomain.pTypePool.size should equal (1)
      keys2.subdomain.pTypePool.values.head should equal (keys2.User)
      keys2.subdomain.eTypePool.size should equal (0)
      keys2.User.keySet.size should equal (2)
      keys2.User.keySet should contain (keys2.User.keys.username)
      keys2.User.keySet should contain (keys2.User.keys.fullName)
    }

    indexes1.subdomain.name should equal ("blogging")
    sets1.subdomain.name should equal ("blogging")
    sets2.subdomain.name should equal ("blogging")

  }

}
