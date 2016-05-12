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

    object shorthands {
      case class Email(email: String)
      val emailShorthand = Shorthand[Email, String]
      implicit val shorthandPool = ShorthandPool(emailShorthand)
    }
    import shorthands._

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

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthands.html
  object shorthands2 {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    object shorthands {
      case class Email(email: String)
      implicit def toEmail(email: String) = Email(email)
      val emailShorthand = Shorthand[Email, String]
      implicit val shorthandPool = ShorthandPool(emailShorthand)
    }
    import shorthands._

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

    val subdomain = Subdomain("blogging", PTypePool(User))
  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/shorthand-pools.html
  object shorthandPools {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    object e1 {
      val pool = ShorthandPool()
    }

    object e2 {
      val pool = ShorthandPool.empty
    }

    object e3 {
      val pool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
    }

    case class User() extends Root
    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    val emailShorthand = Shorthand[Email, String]
    val markdownShorthand = Shorthand[Markdown, String]
    val uriShorthand = Shorthand[Uri, String]

    object e4 {
      implicit val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
      object User extends RootType[User] {
        object keys {
        }
        object indexes {
        }
      }
      val subdomain = Subdomain("blogging", PTypePool(User))
    }

    object e5 {
      import emblem.typeKey
      val shorthandPool = ShorthandPool(emailShorthand, markdownShorthand, uriShorthand)
      object User extends RootType()(typeKey[User], shorthandPool) {
        object keys {
        }
        object indexes {
        }
      }
      val subdomain = Subdomain("blogging", PTypePool(User))(shorthandPool)
    }

  }

  // used in http://longevityframework.github.io/longevity/manual/subdomain/where-not.html
  object shorthandsInitIssues {

    import longevity.subdomain.Shorthand
    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class Email(email: String)
    val emailShorthand = Shorthand[Email, String]
    implicit val shorthandPool = ShorthandPool(emailShorthand)

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

    val subdomain = Subdomain("blogging", PTypePool(User))
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
  import longevity.subdomain.Assoc
  import longevity.subdomain.entity.EntityTypePool
  import longevity.subdomain.Shorthand
  import longevity.subdomain.ShorthandPool
  import longevity.subdomain.Subdomain
  import longevity.subdomain.persistent.Root
  import longevity.subdomain.ptype.RootType

  "user manual example code" should "produce correct subdomains" in {

    {
      shorthands1.subdomain.name should equal ("blogging")
      shorthands1.subdomain.pTypePool.size should equal (1)
      shorthands1.subdomain.pTypePool.values.head should equal (shorthands1.User)
      shorthands1.subdomain.entityTypePool.size should equal (0)
      shorthands1.subdomain.shorthandPool.size should equal (1)
      shorthands1.subdomain.shorthandPool.values.head should equal (shorthands1.shorthands.emailShorthand)
      shorthands1.User.keySet should be ('empty)
    }

    {
      shorthands2.subdomain.name should equal ("blogging")
      shorthands2.subdomain.pTypePool.size should equal (1)
      shorthands2.subdomain.pTypePool.values.head should equal (shorthands2.User)
      shorthands2.subdomain.entityTypePool.size should equal (0)
      shorthands2.subdomain.shorthandPool.size should equal (1)
      shorthands2.subdomain.shorthandPool.values.head should equal (shorthands2.shorthands.emailShorthand)
      shorthands2.User.keySet should be ('empty)
    }

    {
      intercept[java.lang.ExceptionInInitializerError] {
        shorthandsInitIssues.User.keySet should be ('empty)
      }
    }

  }

}
