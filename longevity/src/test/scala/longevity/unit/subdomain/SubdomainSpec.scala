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
      val usernameKey = key("username")
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
      val usernameKey = key("username")
      val fullnameKey = key("firstName", "lastName")
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

    val subdomain = Subdomain("blogging", EntityTypePool(User))
  }

  // used in http://sullivan-.github.io/longevity/manual/subdomain/shorthands.html
  object shorthandPools {
    import longevity.subdomain._

    object e1 {
      val pool = ShorthandPool()
    }

    object e2 {
      val pool = ShorthandPool.empty
    }

    object e3 {
      val pool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
    }

    case class User() extends RootEntity
    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    val emailShorthand = Shorthand[Email, String]
    val markdownShorthand = Shorthand[Markdown, String]
    val uriShorthand = Shorthand[Uri, String]

    // duplicated at https://gist.github.com/sullivan-/5bd434d757dc64b6caac
    object e4 {
      implicit val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
      object User extends RootEntityType[User]
      val subdomain = Subdomain("blogging", EntityTypePool(User))
    }

    // duplicated at https://gist.github.com/sullivan-/76f4dbb5f99af7eaf090
    object e5 {
      import emblem.imports._
      val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
      object User extends RootEntityType()(typeKey[User], shorthandPool)
      val subdomain = Subdomain("blogging", EntityTypePool(User))(shorthandPool)
    }

  }

  // duplicated at https://gist.github.com/sullivan-/62a216ece7a16bec63c9
  // duplicated at https://gist.github.com/sullivan-/ca7bb9e6911ff93b4743
  // duplicated at https://gist.github.com/sullivan-/5b350f2f51ee61efcf8e
  // used in http://sullivan-.github.io/longevity/manual/subdomain/entities.html
  object entities {
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
      profile: Option[UserProfile])
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User, UserProfile))
  }

  // duplicated at https://gist.github.com/sullivan-/95ad8f72bcb4050ccfc3
  // used in http://sullivan-.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects1 {
    import longevity.subdomain._

    case class Email(email: String)
    case class StateCode(stateCode: String)
    case class ZipCode(zipCode: String)
    val emailShorthand = Shorthand[Email, String]
    val stateCodeShorthand = Shorthand[StateCode, String]
    val zipCodeShorthand = Shorthand[ZipCode, String]
    implicit val shorthandPool = ShorthandPool(emailShorthand, stateCodeShorthand, zipCodeShorthand)

    case class Address(
      street: String,
      city: String,
      state: StateCode,
      zip: ZipCode)
    extends ValueObject

    object Address extends ValueType[Address]

    case class User(
      username: String,
      email: Email,
      address: Address)
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User, Address))
  }

  // duplicated at https://gist.github.com/sullivan-/f882ca0f2e4ca103d792
  // used in http://sullivan-.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects2 {
    import longevity.subdomain._

    case class Email(email: String)
    case class StateCode(stateCode: String)
    case class ZipCode(zipCode: String)
    val emailShorthand = Shorthand[Email, String]
    val stateCodeShorthand = Shorthand[StateCode, String]
    val zipCodeShorthand = Shorthand[ZipCode, String]
    implicit val shorthandPool = ShorthandPool(emailShorthand, stateCodeShorthand, zipCodeShorthand)

    case class Address(
      street: String,
      city: String,
      state: StateCode,
      zip: ZipCode)
    extends Entity

    object Address extends EntityType[Address]

    case class User(
      username: String,
      email: Email,
      address: Address)
    extends RootEntity

    object User extends RootEntityType[User]

    val subdomain = Subdomain("blogging", EntityTypePool(User, Address))
  }

  // duplicated at https://gist.github.com/sullivan-/36cbd3871282cda7fe40
  // used in http://sullivan-.github.io/longevity/manual/subdomain/associations.html
  object associations1 {
    import longevity.subdomain._

    case class User(username: String) extends RootEntity
    
    object User extends RootEntityType[User]

    case class Blog(uri: String, authors: Set[Assoc[User]])
    extends RootEntity

    object Blog extends RootEntityType[Blog]

    case class BlogPost(uri: String, blog: Assoc[Blog], authors: Set[Assoc[Blog]])
    extends RootEntity

    object BlogPost extends RootEntityType[BlogPost]

    val subdomain = Subdomain("blogging", EntityTypePool(User, Blog, BlogPost))
  }

  // duplicated at https://gist.github.com/sullivan-/2c6d949bed353aac39ca
  // used in http://sullivan-.github.io/longevity/manual/subdomain/associations.html
  object associations2 {
    import longevity.subdomain._

    case class User(username: String) extends RootEntity

    object User extends RootEntityType[User]

    case class UserProfile(
      user: Assoc[User],
      tagline: String,
      imageUri: String,
      description: String)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    case class Blog(uri: String, authors: Set[UserProfile])
    extends RootEntity

    object Blog extends RootEntityType[Blog]

    val subdomain = Subdomain("blogging", EntityTypePool(User, UserProfile, Blog))
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
      roots.subdomain.rootEntityTypePool.values.head should equal (roots.User)
      roots.User.keys should be ('empty)
    }

    {
      keys1.subdomain.name should equal ("blogging")
      keys1.subdomain.entityTypePool.size should equal (1)
      keys1.subdomain.entityTypePool.values.head should equal (keys1.User)
      keys1.subdomain.rootEntityTypePool.size should equal (1)
      keys1.subdomain.rootEntityTypePool.values.head should equal (keys1.User)
      keys1.User.keys.size should equal (1)
      keys1.User.keys.head should equal (keys1.User.usernameKey)
      keys1.User.usernameKey.props.size should equal (1)
      val prop = keys1.User.usernameKey.props.head
      prop.path should equal ("username")
      prop.typeKey should equal (typeKey[String])
    }

    {
      keys2.subdomain.name should equal ("blogging")
      keys2.subdomain.entityTypePool.size should equal (1)
      keys2.subdomain.entityTypePool.values.head should equal (keys2.User)
      keys2.subdomain.rootEntityTypePool.size should equal (1)
      keys2.subdomain.rootEntityTypePool.values.head should equal (keys2.User)
      keys2.User.keys.size should equal (2)
      keys2.User.keys.find(_.props.size == 1).value should equal (keys2.User.usernameKey)
      keys2.User.usernameKey.props.size should equal (1)
      val usernameKeyProp = keys2.User.usernameKey.props.head
      usernameKeyProp.path should equal ("username")
      usernameKeyProp.typeKey should equal (typeKey[String])

      keys2.User.keys.find(_.props.size == 2).value should equal (keys2.User.fullnameKey)
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
      basics.subdomain.rootEntityTypePool.size should equal (1)
      basics.subdomain.rootEntityTypePool.values.head should equal (basics.User)
      basics.subdomain.shorthandPool should be ('empty)
      basics.User.keys should be ('empty)
    }

    {
      collections.subdomain.name should equal ("blogging")
      collections.subdomain.entityTypePool.size should equal (1)
      collections.subdomain.entityTypePool.values.head should equal (collections.User)
      collections.subdomain.shorthandPool should be ('empty)
      collections.subdomain.rootEntityTypePool.size should equal (1)
      collections.subdomain.rootEntityTypePool.values.head should equal (collections.User)
      collections.User.keys should be ('empty)
    }

    {
      shorthands.subdomain.name should equal ("blogging")
      shorthands.subdomain.entityTypePool.size should equal (1)
      shorthands.subdomain.entityTypePool.values.head should equal (shorthands.User)
      shorthands.subdomain.shorthandPool.size should equal (1)
      shorthands.subdomain.shorthandPool.values.head should equal (shorthands.emailShorthand)
      shorthands.subdomain.rootEntityTypePool.size should equal (1)
      shorthands.subdomain.rootEntityTypePool.values.head should equal (shorthands.User)
      shorthands.User.keys should be ('empty)
    }

    {
      entities.subdomain.name should equal ("blogging")
      entities.subdomain.entityTypePool.size should equal (2)
      entities.subdomain.entityTypePool.values should contain (entities.User)
      entities.subdomain.entityTypePool.values should contain (entities.UserProfile)
      entities.subdomain.shorthandPool.size should equal (3)
      entities.subdomain.shorthandPool.values should contain (entities.emailShorthand)
      entities.subdomain.shorthandPool.values should contain (entities.markdownShorthand)
      entities.subdomain.shorthandPool.values should contain (entities.uriShorthand)
      entities.subdomain.rootEntityTypePool.size should equal (1)
      entities.subdomain.rootEntityTypePool.values.head should equal (entities.User)
      entities.User.keys should be ('empty)
    }

    {
      valueObjects1.subdomain.name should equal ("blogging")
      valueObjects1.subdomain.entityTypePool.size should equal (2)
      valueObjects1.subdomain.entityTypePool.values should contain (valueObjects1.User)
      valueObjects1.subdomain.entityTypePool.values should contain (valueObjects1.Address)
      valueObjects1.subdomain.shorthandPool.size should equal (3)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.emailShorthand)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.stateCodeShorthand)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.zipCodeShorthand)
      valueObjects1.subdomain.rootEntityTypePool.size should equal (1)
      valueObjects1.subdomain.rootEntityTypePool.values.head should equal (valueObjects1.User)
      valueObjects1.User.keys should be ('empty)
    }

    {
      valueObjects2.subdomain.name should equal ("blogging")
      valueObjects2.subdomain.entityTypePool.size should equal (2)
      valueObjects2.subdomain.entityTypePool.values should contain (valueObjects2.User)
      valueObjects2.subdomain.entityTypePool.values should contain (valueObjects2.Address)
      valueObjects2.subdomain.shorthandPool.size should equal (3)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.emailShorthand)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.stateCodeShorthand)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.zipCodeShorthand)
      valueObjects2.subdomain.rootEntityTypePool.size should equal (1)
      valueObjects2.subdomain.rootEntityTypePool.values.head should equal (valueObjects2.User)
      valueObjects2.User.keys should be ('empty)
    }

  }

}
