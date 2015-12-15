package longevity.integration.subdomain.withComplexConstraint

import longevity.subdomain._

case class WithComplexConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root {

  if (!emails.contains(primaryEmail)) throw new ConstraintValidationException("primary email is not in emails")
}

object WithComplexConstraint extends RootType[WithComplexConstraint] {
  key("id")
}

