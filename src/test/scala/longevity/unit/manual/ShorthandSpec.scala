package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

// TODO: figure out what to do with these manual examples

object ShorthandSpec {

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands1 {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends ValueObject
    object Email extends ValueType[Email]

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      primaryEmail: Email,
      emails: Set[Email])
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(Email))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands2 {

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String) extends ValueObject
    implicit def toEmail(email: String) = Email(email)
    object Email extends ValueType[Email]

    val user = User(
      "bolt",
      "Jeremy",
      "Linden",
      "bolt26@info.com",
      Set("bolt26@info.com", "bolt65766@gmail.com"))

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      primaryEmail: Email,
      emails: Set[Email])
    extends Root

    object User extends RootType[User] {
      object props {
      }
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(Email))
  }

}

/** exercises code samples found in the shorthands section of the user manual.
 * the samples themselves are in [[ShorthandSpec]] companion object. we include
 * them in the tests here to force the initialization of the subdomains, and to
 * perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/shorthands
 */
class ShorthandSpec extends FlatSpec with GivenWhenThen with Matchers {

  import ShorthandSpec._

  "user manual example code" should "produce correct subdomains" in {

    {
      shorthands1.subdomain.name should equal ("blogging")
      shorthands1.subdomain.pTypePool.size should equal (1)
      shorthands1.subdomain.pTypePool.values.head should equal (shorthands1.User)
      shorthands1.subdomain.eTypePool.size should equal (1)
      shorthands1.subdomain.eTypePool.values.head should equal (shorthands1.Email)
      shorthands1.User.keySet should be ('empty)
    }

    {
      shorthands2.subdomain.name should equal ("blogging")
      shorthands2.subdomain.pTypePool.size should equal (1)
      shorthands2.subdomain.pTypePool.values.head should equal (shorthands2.User)
      shorthands2.subdomain.eTypePool.size should equal (1)
      shorthands2.subdomain.eTypePool.values.head should equal (shorthands2.Email)
      shorthands2.User.keySet should be ('empty)
    }

  }

}
