package longevity.unit.subdomain

import org.scalatest._
import org.scalatest.OptionValues._

object SubdomainSpec {

  // duplicated at https://gist.github.com/sullivan-/1bf6e826ce266588ecde
  // used in http://sullivan-.github.io/longevity/manual/subdomain/
  object emptySubdomain {
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
      natKey("username")
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
      natKey("username")
      natKey("firstName", "lastName")
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
      def emptySubdomainShould(subdomain: Subdomain, name: String): Unit = {
        subdomain.name should equal (name)
        subdomain.entityTypePool should be ('empty)
        subdomain.shorthandPool should be ('empty)
        subdomain.rootEntityTypePool should be ('empty)
      }

      emptySubdomainShould(emptySubdomain.subdomain, "blogging")
      emptySubdomainShould(emptySubdomain.coreDomain, "blogging")
      emptySubdomainShould(emptySubdomain.supportingSubdomain, "accounts")
      emptySubdomainShould(emptySubdomain.genericSubdomain, "searches")
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
      val key = keys1.User.natKeys.head
      key.props.size should equal (1)
      val prop = key.props.head
      prop.path should equal ("username")
      prop.typeKey should equal (typeKey[String])
    }

    {
      keys2.subdomain.name should equal ("blogging")
      keys2.subdomain.rootEntityTypePool.size should equal (1)
      keys2.subdomain.entityTypePool.values.head should equal (keys2.User)
      keys2.User.natKeys.size should equal (2)
      val usernameKey = keys2.User.natKeys.find(_.props.size == 1).value
      usernameKey.props.size should equal (1)
      val usernameKeyProp = usernameKey.props.head
      usernameKeyProp.path should equal ("username")
      usernameKeyProp.typeKey should equal (typeKey[String])

      val fullnameKey = keys2.User.natKeys.find(_.props.size == 2).value
      fullnameKey.props.size should equal (2)
      val firstNameKeyProp = fullnameKey.props.find(_.path == "firstName").value
      firstNameKeyProp.path should equal ("firstName")
      firstNameKeyProp.typeKey should equal (typeKey[String])
      val lastNameKeyProp = fullnameKey.props.find(_.path == "lastName").value
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

  }

}
