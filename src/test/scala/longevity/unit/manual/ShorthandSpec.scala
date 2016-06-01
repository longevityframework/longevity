package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object ShorthandSpec {

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands1 {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String)
    object Email extends Shorthand[Email, String]

    case class User(
      username: String,
      firstName: String,
      lastName: String,
      primaryEmail: Email,
      emails: Set[Email])
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
      shorthandPool = ShorthandPool(Email))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands2 {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String)
    implicit def toEmail(email: String) = Email(email)
    object Email extends Shorthand[Email, String]

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
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      EntityTypePool(),
      ShorthandPool(Email))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthand-pools.html
  object shorthandPools {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class User() extends Root
    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    object Email extends Shorthand[Email, String]
    object Markdown extends Shorthand[Markdown, String]
    object Uri extends Shorthand[Uri, String]

    object e1 {
      val pool = ShorthandPool()
    }

    object e2 {
      val pool = ShorthandPool.empty
    }

    object e3 {
      val pool = ShorthandPool(Email, Markdown, Uri)
    }

    object e4 {
      val shorthandPool = ShorthandPool(Email, Markdown, Uri)
      object User extends RootType[User] {
        object keys {
        }
        object indexes {
        }
      }
      val subdomain = Subdomain("blogging", PTypePool(User), EntityTypePool(), shorthandPool)
    }

    object e5 {
      import emblem.typeKey
      object User extends RootType()(typeKey[User]) {
        object keys {
        }
        object indexes {
        }
      }
      val subdomain = Subdomain(
        "blogging",
        PTypePool(User),
        EntityTypePool(),
        ShorthandPool(Email, Markdown, Uri))
    }

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
      shorthands1.subdomain.entityTypePool.size should equal (0)
      shorthands1.subdomain.shorthandPool.size should equal (1)
      shorthands1.subdomain.shorthandPool.values.head should equal (shorthands1.Email)
      shorthands1.User.keySet should be ('empty)
    }

    {
      shorthands2.subdomain.name should equal ("blogging")
      shorthands2.subdomain.pTypePool.size should equal (1)
      shorthands2.subdomain.pTypePool.values.head should equal (shorthands2.User)
      shorthands2.subdomain.entityTypePool.size should equal (0)
      shorthands2.subdomain.shorthandPool.size should equal (1)
      shorthands2.subdomain.shorthandPool.values.head should equal (shorthands2.Email)
      shorthands2.User.keySet should be ('empty)
    }

  }

}
