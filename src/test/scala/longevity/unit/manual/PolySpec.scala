package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object PolySpec {

  // used in http://longevityframework.github.io/longevity/manual/poly/index.html
  object poly {

    import longevity.subdomain.embeddable.ValueObject

    case class Email(email: String) extends ValueObject
    case class PhoneNumber(phoneNumber: String) extends ValueObject

    import longevity.subdomain.embeddable.Entity
    import org.joda.time.DateTime

    trait UserVerification extends Entity {
      val verificationDate: DateTime
    }

    case class EmailVerification(
      email: Email,
      verificationDate: DateTime)
    extends UserVerification
 
    case class SmsVerification(
      phoneNumber: PhoneNumber,
      verificationDate: DateTime)
    extends UserVerification

    case class GoogleSignIn(
      email: Email,
      idToken: String,
      verificationDate: DateTime)
    extends UserVerification

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
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.DerivedEType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.PolyEType
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User),
      ETypePool(
        ValueType[Email],
        ValueType[PhoneNumber],
        PolyEType[UserVerification],
        DerivedEType[EmailVerification, UserVerification],
        DerivedEType[SmsVerification, UserVerification],
        DerivedEType[GoogleSignIn, UserVerification]))
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent1 {

    import longevity.subdomain.embeddable.ValueObject

    case class Email(email: String) extends ValueObject
    case class Markdown(markdown: String) extends ValueObject
    case class Uri(uri: String) extends ValueObject

    import longevity.subdomain.embeddable.Entity

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

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
    }

    case class Member(
      username: String,
      email: Email,
      profile: UserProfile)
    extends User

    object Member extends DerivedPType[Member, User] {
      object props {
      }
      object keys {
      }
    }

    case class Commenter(
      username: String,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.EntityType
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User, Member, Commenter),
      ETypePool(
        ValueType[Email],
        ValueType[Markdown],
        ValueType[Uri],
        EntityType[UserProfile]))

  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent2 {

    import longevity.subdomain.embeddable.ValueObject

    case class Email(email: String) extends ValueObject
    case class Markdown(markdown: String) extends ValueObject
    case class Uri(uri: String) extends ValueObject

    import longevity.subdomain.embeddable.Entity

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)
    extends Entity

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
      object props {
      }
      object keys {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.EntityType
    import longevity.subdomain.embeddable.ValueType
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "blogging",
      PTypePool(User, Member, Commenter),
      ETypePool(
        ValueType[Email],
        ValueType[Markdown],
        ValueType[Uri],
        EntityType[UserProfile]))

  }

  // used in http://longevityframework.github.io/longevity/manual/poly/cv.html
  object cv1 {
    sealed trait AccountStatus
    case object Active extends AccountStatus
    case object Suspended extends AccountStatus
    case object Cancelled extends AccountStatus
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/cv.html
  object cv2 {

    import longevity.subdomain.embeddable.Embeddable

    sealed trait AccountStatus extends Embeddable
    case object Active extends AccountStatus
    case object Suspended extends AccountStatus
    case object Cancelled extends AccountStatus

    import longevity.subdomain.persistent.Root
    import longevity.subdomain.ptype.RootType

    case class Account(
      name: String,
      accountStatus: AccountStatus)
    extends Root

    object Account extends RootType[Account] {
      object keys {
      }
      object indexes {
      }
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.embeddable.DerivedEType
    import longevity.subdomain.embeddable.ETypePool
    import longevity.subdomain.embeddable.PolyEType
    import longevity.subdomain.ptype.PTypePool

    val subdomain = Subdomain(
      "accounts",
      PTypePool(Account),
      ETypePool(
        PolyEType[AccountStatus],
        DerivedEType[Active.type, AccountStatus],
        DerivedEType[Suspended.type, AccountStatus],
        DerivedEType[Cancelled.type, AccountStatus]))

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
      persistent1.subdomain.name should equal ("blogging")
      persistent1.subdomain.pTypePool.size should equal (3)
      persistent1.subdomain.eTypePool.size should equal (4)
      persistent1.User.keySet.size should equal (0)
      persistent1.Member.keySet.size should equal (0)
      persistent1.Commenter.keySet.size should equal (0)
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
