package longevity.integration.model.simpleConstraint

import longevity.model.annotations.component

@component[DomainModel]
case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
