package longevity.integration.subdomain.complexConstraint

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class ComplexConstraint(
  id: ComplexConstraintId,
  primaryEmail: Email,
  emails: Set[Email])
extends Root {
  if (!emails.contains(primaryEmail))
    throw new ConstraintValidationException("primary email is not in emails")
}

object ComplexConstraint extends RootType[ComplexConstraint] {
  object props {
    val id = prop[ComplexConstraintId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
