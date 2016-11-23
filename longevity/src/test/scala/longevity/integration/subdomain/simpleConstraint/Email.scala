package longevity.integration.subdomain.simpleConstraint

import longevity.subdomain.annotations.component

@component
case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
