package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object AssocSpec {

  // used in http://longevityframework.github.io/longevity/manual/associations/index.html
  object associations1 {

    import longevity.subdomain.Assoc
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class User(username: String) extends Root
    
    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    case class Blog(uri: String, authors: Set[Assoc[User]])
    extends Root

    object Blog extends RootType[Blog] {
      object keys {
      }
      object indexes {
      }
    }

    case class BlogPost(uri: String, blog: Assoc[Blog], authors: Set[Assoc[Blog]])
    extends Root

    object BlogPost extends RootType[BlogPost] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User, Blog, BlogPost))
  }

  // used in http://longevityframework.github.io/longevity/manual/associations/index.html
  object associations2 {

    import longevity.subdomain.Assoc
    import longevity.subdomain.entity.Entity
    import longevity.subdomain.entity.EntityType
    import longevity.subdomain.entity.EntityTypePool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.PTypePool
    import longevity.subdomain.ptype.RootType

    case class User(username: String) extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    case class UserProfile(
      user: Assoc[User],
      tagline: String,
      imageUri: String,
      description: String)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    case class Blog(uri: String, authors: Set[UserProfile])
    extends Root

    object Blog extends RootType[Blog] {
      object keys {
      }
      object indexes {
      }
    }

    val subdomain = Subdomain("blogging", PTypePool(User, Blog), EntityTypePool(UserProfile))
  }

}

/** exercises code samples found in the associations section of the user manual.
 * the samples themselves are in [[AssocSpec]] companion object. we include
 * them in the tests here to force the initialization of the subdomains, and to
 * perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/associations/
 */
class AssocSpec extends FlatSpec with GivenWhenThen with Matchers {

  import AssocSpec._

  "user manual example code" should "produce correct subdomains" in {
    associations1.subdomain.name should equal ("blogging")
    associations1.subdomain.pTypePool.size should equal (3)
    associations1.subdomain.entityTypePool.size should equal (0)
    associations2.subdomain.name should equal ("blogging")
    associations2.subdomain.pTypePool.size should equal (2)
    associations2.subdomain.entityTypePool.size should equal (1)
  }

}
