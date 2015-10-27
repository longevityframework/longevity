package longevity.integration.withComplexConstraint

import longevity.subdomain._

case class WithComplexConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends RootEntity {

  if (!emails.contains(primaryEmail)) throw new ConstraintValidationException("primary email is not in emails")
}

object WithComplexConstraint extends RootEntityType[WithComplexConstraint] {
  natKey("id")
}

