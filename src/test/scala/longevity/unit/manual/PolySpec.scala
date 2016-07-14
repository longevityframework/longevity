package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object PolySpec {

  // used in http://longevityframework.github.io/longevity/manual/poly/index.html
  object poly {

    import longevity.subdomain.embeddable.ValueObject
    import longevity.subdomain.embeddable.ValueType

    case class Email(email: String) extends ValueObject
    object Email extends ValueType[Email]

    case class PhoneNumber(phoneNumber: String) extends ValueObject
    object PhoneNumber extends ValueType[PhoneNumber]

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
      object props {
      }
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
      ETypePool(Email, PhoneNumber, UserVerification, EmailVerification, SmsVerification, GoogleSignIn))
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent {

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
    import longevity.subdomain.ptype.DerivedPType
    import longevity.subdomain.ptype.PolyPType

    trait User extends Root {
      val username: String
      val email: Email
    }

    object User extends PolyPType[User] {
      object props {
      }
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
      object props {
      }
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
      object props {
      }
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
      PTypePool(User, Member, Commenter),
      ETypePool(Email, Markdown, Uri, UserProfile))

  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent2 {

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

    import longevity.subdomain.KeyVal
    import longevity.subdomain.persistent.Root

    case class Username(username: String)
    extends KeyVal[User, Username](User.keys.username)

    trait User extends Root {
      val username: Username
      val email: Email
    }

    case class Member(
      username: Username,
      email: Email,
      profile: UserProfile)
    extends User

    import longevity.subdomain.ptype.DerivedPType
    import longevity.subdomain.ptype.PolyPType

    object User extends PolyPType[User] {
      object props {
        val username = prop[Username]("username")
        val email = prop[Email]("email")
      }
      object keys {
        val username = key(props.username)
      }
      object indexes {
        val email = index(props.email)
      }
    }

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
      username: Username,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      val polyPType = User
      object props {
      }
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
      PTypePool(User, Member, Commenter),
      ETypePool(Email, Markdown, Uri, UserProfile))

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
      poly.subdomain.eTypePool.size should equal (6)
      poly.User.keySet should be ('empty)
    }

    {
      persistent.subdomain.name should equal ("blogging")
      persistent.subdomain.pTypePool.size should equal (3)
      persistent.subdomain.eTypePool.size should equal (4)
      persistent.User.keySet.size should equal (0)
      persistent.Member.keySet.size should equal (0)
      persistent.Commenter.keySet.size should equal (0)
    }

    {
      persistent2.subdomain.name should equal ("blogging")
      persistent2.subdomain.pTypePool.size should equal (3)
      persistent2.subdomain.eTypePool.size should equal (4)
      persistent2.User.keySet.size should equal (1)
      persistent2.Member.keySet.size should equal (0)
      persistent2.Commenter.keySet.size should equal (0)
    }

  }

}
