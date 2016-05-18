package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain.Shorthand

case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}

object Email extends Shorthand[Email, String]
