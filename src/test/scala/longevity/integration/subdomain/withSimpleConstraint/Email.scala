package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain.embeddable.ValueObject
import longevity.subdomain.embeddable.ValueType

case class Email(email: String) extends ValueObject {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}

object Email extends ValueType[Email]
