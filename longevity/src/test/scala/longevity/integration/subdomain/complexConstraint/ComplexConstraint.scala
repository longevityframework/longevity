package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.PType
import longevity.subdomain.mprops

case class ComplexConstraint(
  id: ComplexConstraintId,
  primaryEmail: Email,
  emails: Set[Email]) {
  if (!emails.contains(primaryEmail))
    throw new ConstraintValidationException("primary email is not in emails")
}

@mprops object ComplexConstraint extends PType[ComplexConstraint] {
  object keys {
    val id = key(props.id)
  }
}
