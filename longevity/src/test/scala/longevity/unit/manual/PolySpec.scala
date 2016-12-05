package longevity.unit.manual

import org.scalatest.FlatSpec
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers

object PolySpec {

  // used in http://longevityframework.github.io/longevity/manual/poly/index.html
  object poly {


    case class Email(email: String)
    case class PhoneNumber(phoneNumber: String)

    import org.joda.time.DateTime

    trait UserVerification {
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

    import longevity.subdomain.PType

    case class User(
      username: String,
      email: Email,
      verifications: List[UserVerification])

    object User extends PType[User] {
      object props {
      }
      val keySet = emptyKeySet
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.DerivedCType
    import longevity.subdomain.CTypePool
    import longevity.subdomain.PolyCType
    import longevity.subdomain.CType
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(
      PTypePool(User),
      CTypePool(
        CType[Email],
        CType[PhoneNumber],
        PolyCType[UserVerification],
        DerivedCType[EmailVerification, UserVerification],
        DerivedCType[SmsVerification, UserVerification],
        DerivedCType[GoogleSignIn, UserVerification]))
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent1 {

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    import longevity.subdomain.DerivedPType
    import longevity.subdomain.PolyPType

    trait User {
      val username: String
      val email: Email
    }

    object User extends PolyPType[User] {
      object props {
      }
      val keySet = emptyKeySet
    }

    case class Member(
      username: String,
      email: Email,
      profile: UserProfile)
    extends User

    object Member extends DerivedPType[Member, User] {
      object props {
      }
      val keySet = emptyKeySet
    }

    case class Commenter(
      username: String,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      object props {
      }
      val keySet = emptyKeySet
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.CTypePool
    import longevity.subdomain.CType
    import longevity.subdomain.CType
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(
      PTypePool(User, Member, Commenter),
      CTypePool(
        CType[Email],
        CType[Markdown],
        CType[Uri],
        CType[UserProfile]))

  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistent.html
  object persistent2 {

    case class Email(email: String)
    case class Markdown(markdown: String)
    case class Uri(uri: String)

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)   

    import longevity.subdomain.KeyVal

    case class Username(username: String) extends KeyVal[User]

    trait User {
      val username: Username
      val email: Email
    }

    case class Member(
      username: Username,
      email: Email,
      profile: UserProfile)
    extends User

    import longevity.subdomain.DerivedPType
    import longevity.subdomain.PolyPType

    object User extends PolyPType[User] {
      object props {
        val username = prop[Username]("username")
        val email = prop[Email]("email")
      }
      val keySet = Set(key(props.username))
      override val indexSet = Set(index(props.email))
    }

    object Member extends DerivedPType[Member, User] {
      object props {
        val tagline = prop[String]("profile.tagline")
      }
      val keySet = emptyKeySet
      override val indexSet = Set(index(props.tagline))
    }

    case class Commenter(
      username: Username,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      object props {
      }
      val keySet = emptyKeySet
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.CTypePool
    import longevity.subdomain.CType
    import longevity.subdomain.CType
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(
      PTypePool(User, Member, Commenter),
      CTypePool(
        CType[Email],
        CType[Markdown],
        CType[Uri],
        CType[UserProfile]))

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

    sealed trait AccountStatus
    case object Active extends AccountStatus
    case object Suspended extends AccountStatus
    case object Cancelled extends AccountStatus

    import longevity.subdomain.PType

    case class Account(
      name: String,
      accountStatus: AccountStatus)   

    object Account extends PType[Account] {
      val keySet = emptyKeySet
    }

    import longevity.subdomain.Subdomain
    import longevity.subdomain.DerivedCType
    import longevity.subdomain.CTypePool
    import longevity.subdomain.PolyCType
    import longevity.subdomain.PTypePool

    val subdomain = Subdomain(
      PTypePool(Account),
      CTypePool(
        PolyCType[AccountStatus],
        DerivedCType[Active.type, AccountStatus],
        DerivedCType[Suspended.type, AccountStatus],
        DerivedCType[Cancelled.type, AccountStatus]))

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
      poly.subdomain.pTypePool.size should equal (1)
      poly.subdomain.pTypePool.values.head should equal (poly.User)
      poly.subdomain.cTypePool.size should equal (6)
      poly.User.keySet should be ('empty)
    }

    {
      persistent1.subdomain.pTypePool.size should equal (3)
      persistent1.subdomain.cTypePool.size should equal (4)
      persistent1.User.keySet.size should equal (0)
      persistent1.Member.keySet.size should equal (0)
      persistent1.Commenter.keySet.size should equal (0)
    }

    {
      persistent2.subdomain.pTypePool.size should equal (3)
      persistent2.subdomain.cTypePool.size should equal (4)
      persistent2.User.keySet.size should equal (1)
      persistent2.Member.keySet.size should equal (0)
      persistent2.Commenter.keySet.size should equal (0)
    }

  }

}
