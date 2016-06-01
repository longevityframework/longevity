package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object SubdomainSpec {

  // used in http://longevityframework.github.io/longevity/manual/subdomain/kinds.html
  object kinds {
    import longevity.subdomain.Subdomain
    val subdomain = Subdomain("blogging")

    // you can also use these synonyms freely:
    import longevity.subdomain.CoreDomain
    import longevity.subdomain.SupportingSubdomain
    import longevity.subdomain.GenericSubdomain

    val coreDomain: CoreDomain = CoreDomain("blogging")
    val supportingSubdomain: SupportingSubdomain = SupportingSubdomain("accounts")
    val genericSubdomain: GenericSubdomain = GenericSubdomain("searches")
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/roots.html
  object roots {

    import longevity.subdomain.persistent.Root

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    import longevity.subdomain.ptype.RootType

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/basics.html
  object basics {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType
    import org.joda.time.DateTime

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      dateJoined: DateTime,
      numCats: Int,
      isSuspended: Boolean = false)
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/collections.html
  object collections {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class User(
      username: String,
      title: Option[String],
      firstName: String,
      lastName: String,
      emails: Set[String])
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/entities.html
  object entities {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    object Email extends Shorthand[Email, String]
    object Markdown extends Shorthand[Markdown, String]
    object Uri extends Shorthand[Uri, String]

    import longevity.subdomain.entity.Entity
    import longevity.subdomain.entity.EntityType

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class User(
      username: String,
      email: Email,
      profile: Option[UserProfile])
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      EntityTypePool(UserProfile),
      ShorthandPool(Email, Markdown, Uri))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects1 {

    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.entity.ValueObject
    import longevity.subdomain.entity.ValueType
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String)
    case class StateCode(stateCode: String)
    case class ZipCode(zipCode: String)
    object Email extends Shorthand[Email, String]
    object StateCode extends Shorthand[StateCode, String]
    object ZipCode extends Shorthand[ZipCode, String]

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
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      EntityTypePool(Address),
      ShorthandPool(Email, StateCode, ZipCode))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects2 {

    import longevity.subdomain.entity.Entity
    import longevity.subdomain.entity.EntityType
    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String)
    case class StateCode(stateCode: String)
    case class ZipCode(zipCode: String)
    object Email extends Shorthand[Email, String]
    object StateCode extends Shorthand[StateCode, String]
    object ZipCode extends Shorthand[ZipCode, String]

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
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      EntityTypePool(Address),
      ShorthandPool(Email, StateCode, ZipCode))
  }

}

/** exercises code samples found in the subdomain section of the user manual.
 * the samples themselves are in [[SubdomainSpec]] companion object. we include
 * them in the tests here to force the initialization of the subdomains, and to
 * perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/subdomain/
 */
class SubdomainSpec extends FlatSpec with GivenWhenThen with Matchers {

  import SubdomainSpec._
  import longevity.subdomain.Subdomain

  "user manual example code" should "produce correct subdomains" in {

    {
      def kindsShould(subdomain: Subdomain, name: String): Unit = {
        subdomain.name should equal (name)
        subdomain.entityTypePool should be ('empty)
        subdomain.shorthandPool should be ('empty)
        subdomain.pTypePool should be ('empty)
      }

      kindsShould(kinds.subdomain, "blogging")
      kindsShould(kinds.coreDomain, "blogging")
      kindsShould(kinds.supportingSubdomain, "accounts")
      kindsShould(kinds.genericSubdomain, "searches")
    }

    {
      roots.subdomain.name should equal ("blogging")
      roots.subdomain.pTypePool.size should equal (1)
      roots.subdomain.pTypePool.values.head should equal (roots.User)
      roots.subdomain.entityTypePool.size should equal (0)
      roots.subdomain.shorthandPool should be ('empty)
      roots.User.keySet should be ('empty)
    }

    {
      basics.subdomain.name should equal ("blogging")
      basics.subdomain.pTypePool.size should equal (1)
      basics.subdomain.pTypePool.values.head should equal (basics.User)
      basics.subdomain.entityTypePool.size should equal (0)
      basics.subdomain.shorthandPool should be ('empty)
      basics.User.keySet should be ('empty)
    }

    {
      collections.subdomain.name should equal ("blogging")
      collections.subdomain.pTypePool.size should equal (1)
      collections.subdomain.pTypePool.values.head should equal (collections.User)
      collections.subdomain.entityTypePool.size should equal (0)
      collections.subdomain.shorthandPool should be ('empty)
      collections.User.keySet should be ('empty)
    }

    {
      entities.subdomain.name should equal ("blogging")
      entities.subdomain.pTypePool.size should equal (1)
      entities.subdomain.pTypePool.values.head should equal (entities.User)
      entities.subdomain.entityTypePool.size should equal (1)
      entities.subdomain.entityTypePool.values should contain (entities.UserProfile)
      entities.subdomain.shorthandPool.size should equal (3)
      entities.subdomain.shorthandPool.values should contain (entities.Email)
      entities.subdomain.shorthandPool.values should contain (entities.Markdown)
      entities.subdomain.shorthandPool.values should contain (entities.Uri)
      entities.User.keySet should be ('empty)
    }

    {
      valueObjects1.subdomain.name should equal ("blogging")
      valueObjects1.subdomain.pTypePool.size should equal (1)
      valueObjects1.subdomain.pTypePool.values.head should equal (valueObjects1.User)
      valueObjects1.subdomain.entityTypePool.size should equal (1)
      valueObjects1.subdomain.entityTypePool.values should contain (valueObjects1.Address)
      valueObjects1.subdomain.shorthandPool.size should equal (3)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.Email)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.StateCode)
      valueObjects1.subdomain.shorthandPool.values should contain (valueObjects1.ZipCode)
      valueObjects1.User.keySet should be ('empty)
    }

    {
      valueObjects2.subdomain.name should equal ("blogging")
      valueObjects2.subdomain.pTypePool.size should equal (1)
      valueObjects2.subdomain.pTypePool.values.head should equal (valueObjects2.User)
      valueObjects2.subdomain.entityTypePool.size should equal (1)
      valueObjects2.subdomain.entityTypePool.values should contain (valueObjects2.Address)
      valueObjects2.subdomain.shorthandPool.size should equal (3)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.Email)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.StateCode)
      valueObjects2.subdomain.shorthandPool.values should contain (valueObjects2.ZipCode)
      valueObjects2.User.keySet should be ('empty)
    }

  }

}
