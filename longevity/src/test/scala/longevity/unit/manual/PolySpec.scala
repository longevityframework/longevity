package longevity.unit.manual

/** code samples found in the entity polymorphism section of the user manual
 *
 * @see http://longevityframework.github.io/longevity/manual/poly/
 */
package PolySpec {

  // used in http://longevityframework.github.io/longevity/manual/poly/components.html
  package components1 {
    case class Email(email: Email)
    case class PhoneNumber(phoneNumber: Email)

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

    case class User(
      username: String,
      email: Email,
      verifications: List[UserVerification])
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/components.html
  package components2 {
    case class Email(email: String)
    case class PhoneNumber(phoneNumber: String)

    import longevity.model.annotations.polyComponent
    import longevity.model.annotations.derivedComponent
    import longevity.model.annotations.persistent
    import org.joda.time.DateTime

    @polyComponent
    trait UserVerification {
      val verificationDate: DateTime
    }

    @derivedComponent[UserVerification]
    case class EmailVerification(
      email: Email,
      verificationDate: DateTime)
         extends UserVerification

    @derivedComponent[UserVerification]
    case class SmsVerification(
      phoneNumber: PhoneNumber,
      verificationDate: DateTime)
         extends UserVerification

    @derivedComponent[UserVerification]
    case class GoogleSignIn(
      email: Email,
      idToken: String,
      verificationDate: DateTime)
         extends UserVerification

    @persistent(keySet = emptyKeySet)
    case class User(
      username: String,
      email: Email,
      verifications: List[UserVerification])
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/components.html
  package components3 {
    case class Email(email: String)
    case class PhoneNumber(phoneNumber: String)

    import longevity.model.PolyCType
    import longevity.model.DerivedCType
    import longevity.model.PType
    import org.joda.time.DateTime

    trait UserVerification {
      val verificationDate: DateTime
    }

    object UserVerification extends PolyCType[UserVerification]

    case class EmailVerification(
      email: Email,
      verificationDate: DateTime)
         extends UserVerification

    object EmailVerification extends DerivedCType[EmailVerification, UserVerification]

    case class SmsVerification(
      phoneNumber: PhoneNumber,
      verificationDate: DateTime)
         extends UserVerification

    object SmsVerification extends DerivedCType[SmsVerification, UserVerification]

    case class GoogleSignIn(
      email: Email,
      idToken: String,
      verificationDate: DateTime)
         extends UserVerification

    object GoogleSignIn extends DerivedCType[GoogleSignIn, UserVerification]

    case class User(
      username: String,
      email: Email,
      verifications: List[UserVerification])

    object User extends PType[User] {
      object props {
        // ...
      }
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistents.html
  package persistents1 {
    case class Uri(uri: String)
    case class Markdown(markdown: String)
    case class Email(email: String)

    @longevity.model.annotations.keyVal[User] case class Username(username: String)

    import longevity.model.annotations.component
    import longevity.model.annotations.derivedPersistent
    import longevity.model.annotations.polyPersistent

    @component
    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    @polyPersistent(keySet = emptyKeySet)
    trait User {
      val username: Username
      val email: Email
    }

    @derivedPersistent[User](keySet = emptyKeySet)
    case class Member(
      username: Username,
      email: Email,
      profile: UserProfile)
    extends User

    @derivedPersistent[User](keySet = emptyKeySet)
    case class Commenter(
      username: Username,
      email: Email)
    extends User
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistents.html
  object persistents2 {
    case class Uri(uri: String)
    case class Markdown(markdown: String)
    case class Email(email: String)

    @longevity.model.annotations.keyVal[User] case class Username(username: String)

    import longevity.model.CType
    import longevity.model.DerivedPType
    import longevity.model.PolyPType

    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    object UserProfile extends CType[UserProfile]

    trait User {
      val username: Username
      val email: Email
    }

    object User extends PolyPType[User] {
      object props {
        // ...
      }
      val keySet = emptyKeySet
    }

    case class Member(
      username: Username,
      email: Email,
      profile: UserProfile)
    extends User

    object Member extends DerivedPType[Member, User] {
      object props {
        // ...
      }
      val keySet = emptyKeySet
    }

    case class Commenter(
      username: Username,
      email: Email)
    extends User

    object Commenter extends DerivedPType[Commenter, User] {
      object props {
        // ...
      }
      val keySet = emptyKeySet
    }
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/persistents.html
  package persistents3 {
    case class Uri(uri: String)
    case class Markdown(markdown: String)
    case class Email(email: String)

    @longevity.model.annotations.keyVal[User] case class Username(username: String)

    import longevity.model.annotations.component
    import longevity.model.annotations.derivedPersistent
    import longevity.model.annotations.polyPersistent

    @component
    case class UserProfile(
      tagline: String,
      imageUri: Uri,
      description: Markdown)

    @polyPersistent(
      keySet = Set(key(props.username)),
      indexSet = Set(index(props.email)))
    trait User {
      val username: Username
      val email: Email
    }

    @derivedPersistent[User](
      keySet = emptyKeySet,
      indexSet = Set(index(props.profile.tagline)))
    case class Member(
      username: Username,
      email: Email,
      profile: UserProfile)
    extends User

    @derivedPersistent[User](keySet = emptyKeySet)
    case class Commenter(
      username: Username,
      email: Email)
    extends User
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/cv.html
  package cv1 {
    sealed trait AccountStatus
    case object Active extends AccountStatus
    case object Suspended extends AccountStatus
    case object Cancelled extends AccountStatus
  }

  // used in http://longevityframework.github.io/longevity/manual/poly/cv.html
  package cv2 {
    import longevity.model.annotations.persistent

    @persistent(keySet = emptyKeySet)
    case class Account(
      name: String,
      accountStatus: AccountStatus)

    import longevity.model.annotations.polyComponent
    import longevity.model.annotations.derivedComponent

    @polyComponent
    sealed trait AccountStatus

    @derivedComponent[AccountStatus]
    case object Active extends AccountStatus

    @derivedComponent[AccountStatus]
    case object Suspended extends AccountStatus

    @derivedComponent[AccountStatus]
    case object Cancelled extends AccountStatus
  }

}
