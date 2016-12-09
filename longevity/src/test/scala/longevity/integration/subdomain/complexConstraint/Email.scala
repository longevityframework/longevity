package longevity.integration.subdomain.complexConstraint

import longevity.model.annotations.component

@component
case class Email(email: String) {

  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")

}
