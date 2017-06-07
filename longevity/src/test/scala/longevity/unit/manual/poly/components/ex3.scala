package longevity.unit.manual.poly.components.ex3

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email(email: String)
case class PhoneNumber(phoneNumber: String)

// end prelude

import longevity.model.DerivedCType
import longevity.model.PType
import longevity.model.PolyCType
import org.joda.time.DateTime

trait UserVerification {
  val verificationDate: DateTime
}

object UserVerification extends PolyCType[DomainModel, UserVerification]

case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

object EmailVerification extends DerivedCType[DomainModel, EmailVerification, UserVerification]

case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

object SmsVerification extends DerivedCType[DomainModel, SmsVerification, UserVerification]

case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

object GoogleSignIn extends DerivedCType[DomainModel, GoogleSignIn, UserVerification]

case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])

object User extends PType[DomainModel, User] {
  object props {
    // ...
  }
}
