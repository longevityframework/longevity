package longevity.unit.manual.poly.components.ex1

case class Email(email: String)
case class PhoneNumber(phoneNumber: String)

// end prelude

import org.joda.time.DateTime

sealed trait UserVerification {
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
