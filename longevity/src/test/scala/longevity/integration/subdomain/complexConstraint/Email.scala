package longevity.integration.subdomain.complexConstraint


case class Email(email: String) {
  if (!email.contains('@')) throw new ConstraintValidationException("no '@' in email")
}
