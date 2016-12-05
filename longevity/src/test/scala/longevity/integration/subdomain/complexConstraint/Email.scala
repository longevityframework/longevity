package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.annotations.component

@component
case class Email(email: String) {

  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")

}
