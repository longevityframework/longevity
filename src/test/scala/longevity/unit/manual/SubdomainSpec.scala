package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object SubdomainSpec {

  // used in http://longevityframework.github.io/longevity/manual/subdomain.html
  object subdomain1 {
    import longevity.subdomain.Subdomain

    val subdomain = Subdomain("blogging")
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain.html
  object subdomain2 {
    import longevity.subdomain.Subdomain

    // create your own domain type:

    class BloggingDomain extends Subdomain("blogging")

    val bloggingDomain = new BloggingDomain

    // or put your subdomains in companion objects:

    object BloggingDomain extends Subdomain("blogging")
  }

  // used in http://longevityframework.github.io/longevity/manual/ptype/ptypes.html
  object ptypes {

    import longevity.subdomain.persistent.Root

    case class User(
      username: String,
      firstName: String,
      lastName: String)
    extends Root

    import longevity.subdomain.ptype.RootType

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/basics.html
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
      object props {
      }
      object keys {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/collections.html
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
      object props {
      }
      object keys {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/embeddables/index.html
  object embeddables1 {

    import longevity.subdomain.embeddable.Embeddable
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class FullName(
      firstName: String,
      lastName: String)
    extends Embeddable

    case class User(
      username: String,
      fullName: FullName)
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(EType[FullName]))

    object FullName extends EType[FullName]

    val subdomain2 = Subdomain("blogging", PTypePool(User), ETypePool(FullName))

  }

  // used in http://longevityframework.github.io/longevity/manual/embeddables/index.html
  object embeddables2 {

    import longevity.subdomain.embeddable.Embeddable
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends Embeddable

    case class EmailPreferences(
      primaryEmail: Email,
      emails: Set[Email])
    extends Embeddable

    case class Address(
      street: String,
      city: String)
    extends Embeddable

    case class User(
      username: String,
      emails: EmailPreferences,
      addresses: Set[Address])
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(EType[Address], EType[Email], EType[EmailPreferences]))

  }

  // used in http://longevityframework.github.io/longevity/manual/embeddables/entities.html
  object entities {

    import longevity.subdomain.embeddable.Entity

    case class UserProfile(
      tagline: String,
      imageUri: String,
      description: String)
    extends Entity

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class User(
      username: String,
      email: String,
      profile: Option[UserProfile])
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EntityType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User), ETypePool(EntityType[UserProfile]))
  }

  // used in http://longevityframework.github.io/longevity/manual/embeddables/value-objects.html
  object valueObjects {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends ValueObject
    case class StateCode(stateCode: String) extends ValueObject
    case class ZipCode(zipCode: String) extends ValueObject

    case class Address(
      street: String,
      city: String,
      state: StateCode,
      zip: ZipCode)
    extends ValueObject

    case class User(
      username: String,
      email: Email,
      address: Address)
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(ValueType[Email], ValueType[StateCode], ValueType[ZipCode], ValueType[Address]))
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
      def subdomainShould(subdomain: Subdomain, name: String): Unit = {
        subdomain.name should equal (name)
        subdomain.eTypePool should be ('empty)
        subdomain.pTypePool should be ('empty)
      }

      subdomainShould(subdomain1.subdomain, "blogging")

      subdomainShould(subdomain2.bloggingDomain, "blogging")
      subdomainShould(subdomain2.BloggingDomain, "blogging")
    }

    {
      ptypes.subdomain.name should equal ("blogging")
      ptypes.subdomain.pTypePool.size should equal (1)
      ptypes.subdomain.pTypePool.values.head should equal (ptypes.User)
      ptypes.subdomain.eTypePool should be ('empty)
      ptypes.User.keySet should be ('empty)
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
      embeddables1.subdomain.name should equal ("blogging")
      embeddables1.subdomain.pTypePool.size should equal (1)
      embeddables1.subdomain.pTypePool.values.head should equal (embeddables1.User)
      embeddables1.subdomain.eTypePool.size should equal (1)
      embeddables1.User.keySet should be ('empty)
    }

    {
      embeddables2.subdomain.name should equal ("blogging")
      embeddables2.subdomain.pTypePool.size should equal (1)
      embeddables2.subdomain.pTypePool.values.head should equal (embeddables2.User)
      embeddables2.subdomain.eTypePool.size should equal (3)
      embeddables2.User.keySet should be ('empty)
    }

    {
      entities.subdomain.name should equal ("blogging")
      entities.subdomain.pTypePool.size should equal (1)
      entities.subdomain.pTypePool.values.head should equal (entities.User)
      entities.subdomain.eTypePool.size should equal (1)
      entities.User.keySet should be ('empty)
    }

    {
      valueObjects.subdomain.name should equal ("blogging")
      valueObjects.subdomain.pTypePool.size should equal (1)
      valueObjects.subdomain.pTypePool.values.head should equal (valueObjects.User)
      valueObjects.subdomain.eTypePool.size should equal (4)
      valueObjects.User.keySet should be ('empty)
    }

  }

}
