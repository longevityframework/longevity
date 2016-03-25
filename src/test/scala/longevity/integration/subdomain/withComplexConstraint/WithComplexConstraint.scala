package longevity.integration.subdomain.withComplexConstraint

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithComplexConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root {
  if (!emails.contains(primaryEmail))
    throw new ConstraintValidationException("primary email is not in emails")
}

object WithComplexConstraint extends RootType[WithComplexConstraint] {
  object props {
    val id = prop[String]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
