package longevity.integration.subdomain.simpleConstraint

import longevity.subdomain.embeddable.Embeddable

case class Email(email: String) extends Embeddable {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
