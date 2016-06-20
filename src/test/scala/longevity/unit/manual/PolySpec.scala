package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object PolySpec {

  // used in http://longevityframework.github.io/longevity/manual/poly/index.html
  object poly {

    import longevity.subdomain.Shorthand

    case class Email(email: String)
    object Email extends Shorthand[Email, String]

    case class PhoneNumber(phoneNumber: String)
    object PhoneNumber extends Shorthand[PhoneNumber, String]

    import longevity.subdomain.embeddable.DerivedType
    import longevity.subdomain.embeddable.Entity
    import longevity.subdomain.embeddable.PolyType
    import org.joda.time.DateTime

    trait UserVerification extends Entity {
      val verificationDate: DateTime
    }

    object UserVerification extends PolyType[UserVerification]

    case class EmailVerification(
      email: Email,
      verificationDate: DateTime)
    extends UserVerification

    object EmailVerification extends DerivedType[EmailVerification, UserVerification] {
      val polyType = UserVerification
    }

    case class SmsVerification(
      phoneNumber: PhoneNumber,
      verificationDate: DateTime)
    extends UserVerification

    object SmsVerification extends DerivedType[SmsVerification, UserVerification] {
      val polyType = UserVerification
    }

    case class GoogleSignIn(
      email: Email,
      idToken: String,
      verificationDate: DateTime)
         extends UserVerification

    object GoogleSignIn extends DerivedType[GoogleSignIn, UserVerification] {
      val polyType = UserVerification
    }

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class User(
      username: String,
      email: Email,
      verifications: List[UserVerification])
    extends Root

    object User extends RootType[User] {
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EntityTypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      EntityTypePool(UserVerification, EmailVerification, SmsVerification, GoogleSignIn),
      ShorthandPool(Email, PhoneNumber))
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent {

    import longevity.subdomain.Shorthand

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    object Email extends Shorthand[Email, String]
    object Markdown extends Shorthand[Markdown, String]
    object Uri extends Shorthand[Uri, String]

    import longevity.subdomain.embeddable.Entity
    import longevity.subdomain.embeddable.EntityType

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.DerivedPType
    import longevity.subdomain.ptype.PolyPType

    trait User extends Root {
      val username: String
      val email: Email
    }

    object User extends PolyPType[User] {
      object keys {
      }
      object indexes {
      }
    }

    case class Member(
      username: String,
      email: Email,
      profile: UserProfile)
    extends User

    object Member extends DerivedPType[Member, User] {
      val polyPType = User
      object keys {
      }
      object indexes {
      }
    }

    case class Commenter(
      username: String,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      val polyPType = User
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EntityTypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User, Member, Commenter),
      EntityTypePool(UserProfile),
      ShorthandPool(Email, Markdown, Uri))

  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent2 {

    import longevity.subdomain.Shorthand

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)
    object Email extends Shorthand[Email, String]
    object Markdown extends Shorthand[Markdown, String]
    object Uri extends Shorthand[Uri, String]

    import longevity.subdomain.embeddable.Entity
    import longevity.subdomain.embeddable.EntityType

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

    object UserProfile extends EntityType[UserProfile]

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.DerivedPType
    import longevity.subdomain.ptype.PolyPType

    trait User extends Root {
      val username: String
      val email: Email
    }

    object User extends PolyPType[User] {
      object props {
        val username = prop[String]("username")
        val email = prop[Email]("email")
      }
      object keys {
        val username = key(props.username)
      }
      object indexes {
        val email = index(props.email)
      }
    }

    case class Member(
      username: String,
      email: Email,
      profile: UserProfile)
    extends User

    object Member extends DerivedPType[Member, User] {
      val polyPType = User
      object props {
        val tagline = prop[String]("profile.tagline")
      }
      object keys {
      }
      object indexes {
        val tagline = index(props.tagline)
      }
    }

    case class Commenter(
      username: String,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      val polyPType = User
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.ShorthandPool
    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.EntityTypePool
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User, Member, Commenter),
      EntityTypePool(UserProfile),
      ShorthandPool(Email, Markdown, Uri))

  }

}

/** exercises code samples found in the entity polymorphism section of the user
 * manual. the samples themselves are in [[PolySpec]] companion object. we
 * include them in the tests here to force the initialization of the subdomains,
 * and to perform some basic sanity checks on the results.
 *
 * @see http://longevityframework.github.io/longevity/manual/poly/
 */
class PolySpec extends FlatSpec with GivenWhenThen with Matchers {

  import PolySpec._

  "user manual example code" should "produce correct subdomains" in {

    {
      poly.subdomain.name should equal ("blogging")
      poly.subdomain.pTypePool.size should equal (1)
      poly.subdomain.pTypePool.values.head should equal (poly.User)
      poly.subdomain.entityTypePool.size should equal (4)
      poly.subdomain.shorthandPool.size should equal (2)
      poly.User.keySet should be ('empty)
    }

    {
      persistent.subdomain.name should equal ("blogging")
      persistent.subdomain.pTypePool.size should equal (3)
      persistent.subdomain.entityTypePool.size should equal (1)
      persistent.subdomain.shorthandPool.size should equal (3)
      persistent.User.keySet.size should equal (0)
      persistent.Member.keySet.size should equal (0)
      persistent.Commenter.keySet.size should equal (0)
    }

    {
      persistent2.subdomain.name should equal ("blogging")
      persistent2.subdomain.pTypePool.size should equal (3)
      persistent2.subdomain.entityTypePool.size should equal (1)
      persistent2.subdomain.shorthandPool.size should equal (3)
      persistent2.User.keySet.size should equal (1)
      persistent2.Member.keySet.size should equal (0)
      persistent2.Commenter.keySet.size should equal (0)
    }

  }

}
