package longevity.integration.withComplexConstraint

case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
