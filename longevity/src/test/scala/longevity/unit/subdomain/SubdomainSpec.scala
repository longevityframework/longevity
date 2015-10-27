package longevity.unit.subdomain

import org.scalatest._
import org.scalatest.OptionValues._

object SubdomainSpec {

  // duplicated at https://gist.github.com/sullivan-/1bf6e826ce266588ecde
  // used in http://sullivan-.github.io/longevity/manual/subdomain/kinds.html
  object kinds {
    import longevity.subdomain._
    val subdomain = Subdomain("blogging", EntityTypePool.empty)

    // you can also use these synonyms freely:
    val coreDomain: CoreDomain = CoreDomain("blogging", EntityTypePool.empty)
    val supportingSubdomain: SupportingSubdomain = SupportingSubdomain("accounts", EntityTypePool.empty)
    val genericSubdomain: GenericSubdomain = GenericSubdomain("searches", EntityTypePool.empty)
  }

  // duplicated at https://gist.github.com/sullivan-/db1226b4d31a0526ac8c
  // duplicated at https://gist.github.com/sullivan-/6a68ac5f6f6331274e21
  // used in http://sullivan-.github.io/longevity/manual/subdomain/roots.html
  object roots {

    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/e2ef663857157a03a301
  // used in http://sullivan-.github.io/longevity/manual/subdomain/keys.html
  object keys1 {
    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends RootEntity

    object User extends RootEntityType[User] {
      val usernameKey = natKey("username")
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/b72900a6882b557e6728
  // used in http://sullivan-.github.io/longevity/manual/subdomain/keys.html
  object keys2 {
    import longevity.subdomain._

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends RootEntity

    object User extends RootEntityType[User] {
      val usernameKey = natKey("username")
      val fullnameKey = natKey("firstName", "lastName")
    }

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/58f8ae308d9ca96dbd63
  // used in http://sullivan-.github.io/longevity/manual/subdomain/basics.html
  object basics {

    import longevity.subdomain._
    import org.joda.time.DateTime

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      dateJoined: DateTime,
      numCats: Int,
      isSuspended: Boolean = false)
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/bfe3bb8ea95f6b7a4834
  // used in http://sullivan-.github.io/longevity/manual/subdomain/collections.html
  object collections {

    import longevity.subdomain._

    case class User(
      username: String,
      title: Option[String],
      firstName: String,
      lastName: String,
      emails: Set[String])
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // duplicated at https://gist.github.com/sullivan-/d1a59a70bbfbcc1e0f78
  // used in http://sullivan-.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands {

    import longevity.subdomain._

    case class Email(email: String)

    val emailShorthand = Shorthand[Email, String]

    implicit val shorthandPool = ShorthandPool(emailShorthand)

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      primaryEmail: Email,
      emails: Set[Email])
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User), shorthandPool)
  }

}

/** exercises code samples found in the subdomain section of the user manual. the samples themselves are
 * in [[SubdomainSpec]] companion object. we include them in the tests here to force the initialization of the
 * subdomains, and to perform some basic sanity checks on the results.
 *
 * @see http://sullivan-.github.io/longevity/manual/subdomain/
 */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  import SubdomainSpec._
  import emblem.imports._
  import longevity.subdomain._

  "user manual example code" should "produce correct subdomains" in {

    {
      def kindsShould(subdomain: Subdomain, name: String): Unit = {
        subdomain.name should equal (name)
        subdomain.entityTypePool should be ('empty)
        subdomain.shorthandPool should be ('empty)
        subdomain.rootEntityTypePool should be ('empty)
      }

      kindsShould(kinds.subdomain, "blogging")
      kindsShould(kinds.coreDomain, "blogging")
      kindsShould(kinds.supportingSubdomain, "accounts")
      kindsShould(kinds.genericSubdomain, "searches")
    }

    {
      roots.subdomain.name should equal ("blogging")
      roots.subdomain.entityTypePool.size should equal (1)
      roots.subdomain.entityTypePool.values.head should equal (roots.User)
      roots.subdomain.shorthandPool should be ('empty)
      roots.subdomain.rootEntityTypePool.size should equal (1)
      roots.User.natKeys should be ('empty)
    }

    {
      keys1.subdomain.name should equal ("blogging")
      keys1.subdomain.rootEntityTypePool.size should equal (1)
      keys1.subdomain.entityTypePool.values.head should equal (keys1.User)
      keys1.User.natKeys.size should equal (1)
      keys1.User.natKeys.head should equal (keys1.User.usernameKey)
      keys1.User.usernameKey.props.size should equal (1)
      val prop = keys1.User.usernameKey.props.head
      prop.path should equal ("username")
      prop.typeKey should equal (typeKey[String])
    }

    {
      keys2.subdomain.name should equal ("blogging")
      keys2.subdomain.rootEntityTypePool.size should equal (1)
      keys2.subdomain.entityTypePool.values.head should equal (keys2.User)
      keys2.User.natKeys.size should equal (2)
      keys2.User.natKeys.find(_.props.size == 1).value should equal (keys2.User.usernameKey)
      keys2.User.usernameKey.props.size should equal (1)
      val usernameKeyProp = keys2.User.usernameKey.props.head
      usernameKeyProp.path should equal ("username")
      usernameKeyProp.typeKey should equal (typeKey[String])

      keys2.User.natKeys.find(_.props.size == 2).value should equal (keys2.User.fullnameKey)
      keys2.User.fullnameKey.props.size should equal (2)
      val firstNameKeyProp = keys2.User.fullnameKey.props.find(_.path == "firstName").value
      firstNameKeyProp.path should equal ("firstName")
      firstNameKeyProp.typeKey should equal (typeKey[String])
      val lastNameKeyProp = keys2.User.fullnameKey.props.find(_.path == "lastName").value
      lastNameKeyProp.path should equal ("lastName")
      lastNameKeyProp.typeKey should equal (typeKey[String])
    }

    {
      basics.subdomain.name should equal ("blogging")
      basics.subdomain.entityTypePool.size should equal (1)
      basics.subdomain.entityTypePool.values.head should equal (basics.User)
      basics.subdomain.shorthandPool should be ('empty)
      basics.subdomain.rootEntityTypePool.size should equal (1)
      basics.User.natKeys should be ('empty)
    }

    {
      collections.subdomain.name should equal ("blogging")
      collections.subdomain.entityTypePool.size should equal (1)
      collections.subdomain.entityTypePool.values.head should equal (collections.User)
      collections.subdomain.shorthandPool should be ('empty)
      collections.subdomain.rootEntityTypePool.size should equal (1)
      collections.User.natKeys should be ('empty)
    }

    {
      shorthands.subdomain.name should equal ("blogging")
      shorthands.subdomain.entityTypePool.size should equal (1)
      shorthands.subdomain.entityTypePool.values.head should equal (shorthands.User)
      shorthands.subdomain.shorthandPool.size should equal (1)
      shorthands.subdomain.shorthandPool.values.head should equal (shorthands.emailShorthand)
      shorthands.subdomain.rootEntityTypePool.size should equal (1)
      shorthands.User.natKeys should be ('empty)
    }

  }

}
