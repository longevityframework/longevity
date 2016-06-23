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
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(Email, Markdown, Uri, UserProfile))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects1 {

    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends ValueObject
    object Email extends ValueType[Email]

    case class StateCode(stateCode: String) extends ValueObject
    object StateCode extends ValueType[StateCode]

    case class ZipCode(zipCode: String) extends ValueObject
    object ZipCode extends ValueType[ZipCode]

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
      ETypePool(Email, StateCode, ZipCode, Address))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/value-objects.html
  object valueObjects2 {

    import longevity.subdomain.embeddable.Entity
    import longevity.subdomain.embeddable.EntityType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends ValueObject
    object Email extends ValueType[Email]

    case class StateCode(stateCode: String) extends ValueObject
    object StateCode extends ValueType[StateCode]

    case class ZipCode(zipCode: String) extends ValueObject
    object ZipCode extends ValueType[ZipCode]

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
      ETypePool(Email, StateCode, ZipCode, Address))
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
        subdomain.eTypePool should be ('empty)
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
      roots.subdomain.eTypePool should be ('empty)
      roots.User.keySet should be ('empty)
    }

    {
      basics.subdomain.name should equal ("blogging")
      basics.subdomain.pTypePool.size should equal (1)
      basics.subdomain.pTypePool.values.head should equal (basics.User)
      basics.subdomain.eTypePool should be ('empty)
      basics.User.keySet should be ('empty)
    }

    {
      collections.subdomain.name should equal ("blogging")
      collections.subdomain.pTypePool.size should equal (1)
      collections.subdomain.pTypePool.values.head should equal (collections.User)
      collections.subdomain.eTypePool should be ('empty)
      collections.User.keySet should be ('empty)
    }

    {
      entities.subdomain.name should equal ("blogging")
      entities.subdomain.pTypePool.size should equal (1)
      entities.subdomain.pTypePool.values.head should equal (entities.User)
      entities.subdomain.eTypePool.size should equal (4)
      entities.subdomain.eTypePool.values should contain (entities.UserProfile)
      entities.subdomain.eTypePool.values should contain (entities.Email)
      entities.subdomain.eTypePool.values should contain (entities.Markdown)
      entities.subdomain.eTypePool.values should contain (entities.Uri)
      entities.User.keySet should be ('empty)
    }

    {
      valueObjects1.subdomain.name should equal ("blogging")
      valueObjects1.subdomain.pTypePool.size should equal (1)
      valueObjects1.subdomain.pTypePool.values.head should equal (valueObjects1.User)
      valueObjects1.subdomain.eTypePool.size should equal (4)
      valueObjects1.subdomain.eTypePool.values should contain (valueObjects1.Address)
      valueObjects1.subdomain.eTypePool.values should contain (valueObjects1.Email)
      valueObjects1.subdomain.eTypePool.values should contain (valueObjects1.StateCode)
      valueObjects1.subdomain.eTypePool.values should contain (valueObjects1.ZipCode)
      valueObjects1.User.keySet should be ('empty)
    }

    {
      valueObjects2.subdomain.name should equal ("blogging")
      valueObjects2.subdomain.pTypePool.size should equal (1)
      valueObjects2.subdomain.pTypePool.values.head should equal (valueObjects2.User)
      valueObjects2.subdomain.eTypePool.size should equal (4)
      valueObjects2.subdomain.eTypePool.values should contain (valueObjects2.Address)
      valueObjects2.subdomain.eTypePool.values should contain (valueObjects2.Email)
      valueObjects2.subdomain.eTypePool.values should contain (valueObjects2.StateCode)
      valueObjects2.subdomain.eTypePool.values should contain (valueObjects2.ZipCode)
      valueObjects2.User.keySet should be ('empty)
    }

  }

}
