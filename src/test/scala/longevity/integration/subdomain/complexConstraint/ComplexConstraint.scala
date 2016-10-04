package longevity.integration.subdomain.complexConstraint

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.PType

case class ComplexConstraint(
  id: ComplexConstraintId,
  primaryEmail: Email,
  emails: Set[Email])
extends Root {
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
