package longevity.integration.withSimpleConstraint

case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
