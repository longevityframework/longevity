package longevity.unit.manual.poly.components.ex2

import longevity.model.annotations.domainModel
@domainModel trait DomainModel

case class Email(email: String)
case class PhoneNumber(phoneNumber: String)

// end prelude

import longevity.model.annotations.polyComponent
import longevity.model.annotations.derivedComponent
import longevity.model.annotations.persistent
import org.joda.time.DateTime

@polyComponent[DomainModel]
sealed trait UserVerification {
  val verificationDate: DateTime
}

@derivedComponent[DomainModel, UserVerification]
case class EmailVerification(
  email: Email,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[DomainModel, UserVerification]
case class SmsVerification(
  phoneNumber: PhoneNumber,
  verificationDate: DateTime)
extends UserVerification

@derivedComponent[DomainModel, UserVerification]
case class GoogleSignIn(
  email: Email,
  idToken: String,
  verificationDate: DateTime)
extends UserVerification

@persistent[DomainModel]
case class User(
  username: String,
  email: Email,
  verifications: List[UserVerification])
