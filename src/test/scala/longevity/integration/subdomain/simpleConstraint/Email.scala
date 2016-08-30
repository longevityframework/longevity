package longevity.integration.subdomain.simpleConstraint

import longevity.subdomain.embeddable.ValueObject

case class Email(email: String) extends ValueObject {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
