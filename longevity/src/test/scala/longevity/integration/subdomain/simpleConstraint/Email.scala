package longevity.integration.subdomain.simpleConstraint


case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
