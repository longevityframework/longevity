package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.PType

case class ComplexConstraint(
  id: ComplexConstraintId,
  primaryEmail: Email,
  emails: Set[Email]) {
  if (!emails.contains(primaryEmail))
    throw new ConstraintValidationException("primary email is not in emails")
}

object ComplexConstraint extends PType[ComplexConstraint] {
  object props {
    val id = prop[ComplexConstraintId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
