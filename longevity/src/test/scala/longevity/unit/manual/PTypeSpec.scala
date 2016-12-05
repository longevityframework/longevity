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

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)

    import longevity.subdomain.PType

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    case class User(
      username: String,
      email: Email,
      profile: UserProfile)   

    object User extends PType[User] {
      object props {

       // fully typed:
       val profileDescription: longevity.subdomain.ptype.Prop[User, Markdown] =
         prop[Markdown]("profile.description")

       // brief:
       val usernameProp = prop[String]("username")
      }
      val keySet = emptyKeySet
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.CTypePool
    import longevity.subdomain.CType
    import longevity.subdomain.CType
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(
      PTypePool(User),
      CTypePool(CType[Email], CType[Markdown], CType[Uri], CType[UserProfile]))

  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  object keys1 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class User(
      username: Username,
      firstName: String,
      lastName: String)   

    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
      }
      val keySet = Set(key(props.username))
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/keys.html
  object keys2 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class FullName(first: String, last: String) extends KeyVal[User]

    case class User(
      username: Username,
      fullName: FullName)   

    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        val fullName = prop[FullName]("fullName")
      }
      val keySet = Set(key(props.username), key(props.fullName))
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/indexes.html
  object indexes1 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class User(
      username: Username,
      firstName: String,
      lastName: String)

    object User extends PType[User] {
      object props {
        val username = prop[Username]("username")
        val firstName = prop[String]("firstName")
        val lastName = prop[String]("lastName")
      }
      val keySet = Set(key(props.username))
      override val indexSet = Set(index(props.lastName, props.firstName))
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/root-type/key-sets-and-index-sets.html
  object sets1 {

    case class User(
      username: String,
      firstName: String,
      lastName: String)

    import longevity.subdomain.ptype.Index
    import longevity.subdomain.ptype.Key
    import longevity.subdomain.ptype.Prop
    import longevity.subdomain.PType

    object User extends PType[User] {
      override lazy val propSet = Set.empty[Prop[User, _]]
      override val keySet = Set.empty[Key[User]]
      override val indexSet = Set.empty[Index[User]]
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/root-type/key-sets-and-index-sets.html
  object sets2 {

    import longevity.subdomain.KeyVal
    import longevity.subdomain.ptype.Key
    import longevity.subdomain.ptype.Prop
    import longevity.subdomain.PType

    case class Username(username: String) extends KeyVal[User]

    case class Email(email: String) extends KeyVal[User]

    case class User(
      username: Username,
      email: Email,
      firstName: String,
      lastName: String)

    object User extends PType[User] {
      val usernameProp = prop[Username]("username")
      val emailProp = prop[Email]("email")
      val firstNameProp = prop[String]("firstName")
      val lastNameProp = prop[String]("lastName")

      val usernameKey = key(usernameProp)
      val emailKey = key(emailProp)
      val fullnameIndex = index(lastNameProp, firstNameProp)

      override lazy val propSet = Set[Prop[User, _]](usernameProp, emailProp, firstNameProp, lastNameProp)
      override val keySet = Set[Key[User]](usernameKey, emailKey)
      override val indexSet = Set(fullnameIndex)
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(PTypePool(User))
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
      properties.subdomain.pTypePool.size should equal (1)
      properties.subdomain.pTypePool.values.head should equal (properties.User)
      properties.subdomain.cTypePool.size should equal (4)
      properties.User.keySet should be ('empty)
    }

    {
      keys1.subdomain.pTypePool.size should equal (1)
      keys1.subdomain.pTypePool.values.head should equal (keys1.User)
      keys1.subdomain.cTypePool.size should equal (0)
      keys1.User.keySet.size should equal (1)
    }

    {
      keys2.subdomain.pTypePool.size should equal (1)
      keys2.subdomain.pTypePool.values.head should equal (keys2.User)
      keys2.subdomain.cTypePool.size should equal (0)
      keys2.User.keySet.size should equal (2)
    }

  }

}
