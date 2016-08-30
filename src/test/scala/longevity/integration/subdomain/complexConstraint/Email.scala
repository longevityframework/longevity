package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.embeddable.ValueObject

case class Email(email: String) extends ValueObject {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
