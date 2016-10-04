package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

/** code samples found in the key value section of the user manual */
object KeyValSpec {

  // duplicated at http://longevityframework.github.io/longevity/manual/key-values.html
  object keyValues1 {

    import longevity.subdomain.KeyVal

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    import longevity.ddd.subdomain.Root

    case class User(
      username: Username,
      firstName: String,
      lastName: String)
    extends Root

    import longevity.subdomain.ptype.RootType

    object User extends RootType[User] {
      object props {
        val username = prop[Username]("username")
      }
      object keys {
        val username = key(props.username)
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))    

  }

  // duplicated at http://longevityframework.github.io/longevity/manual/key-values.html
  object keyValues2 {

    import longevity.subdomain.KeyVal

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    import longevity.ddd.subdomain.Root

    case class User(
      username: Username,
      firstName: String,
      lastName: String,
      sponsor: Option[Username])
    extends Root

    import longevity.subdomain.ptype.RootType

    object User extends RootType[User] {
      object props {
        val username = prop[Username]("username")
      }
      object keys {
        val username = key(props.username)
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain("blogging", PTypePool(User))    

  }

}

/** exercises code samples found in the key value section of the user manual.
 *
 * @see http://longevityframework.github.io/longevity/manual/key-values.html
 */
class KeyValSpec extends FlatSpec with GivenWhenThen with Matchers {

  import KeyValSpec._

  "user manual example code" should "produce correct subdomains" in {

    {
      keyValues1.subdomain.name should equal ("blogging")
      keyValues1.subdomain.pTypePool.size should equal (1)
      keyValues1.subdomain.pTypePool.values.head should equal (keyValues1.User)
      keyValues1.subdomain.eTypePool should be ('empty)
      keyValues1.User.keySet.size should equal (1)
      keyValues1.User.keySet.head should equal (keyValues1.User.keys.username)
    }

    {
      keyValues2.subdomain.name should equal ("blogging")
      keyValues2.subdomain.pTypePool.size should equal (1)
      keyValues2.subdomain.pTypePool.values.head should equal (keyValues2.User)
      keyValues2.subdomain.eTypePool should be ('empty)
      keyValues2.User.keySet.size should equal (1)
      keyValues2.User.keySet.head should equal (keyValues2.User.keys.username)
    }

  }
}
