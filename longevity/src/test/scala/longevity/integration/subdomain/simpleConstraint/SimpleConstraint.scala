package longevity.integration.subdomain.simpleConstraint

import longevity.subdomain.PType
import longevity.subdomain.mprops

case class SimpleConstraint(
  id: SimpleConstraintId,
  primaryEmail: Email,
  emails: Set[Email])

@mprops object SimpleConstraint extends PType[SimpleConstraint] {
  object keys {
    val id = key(props.id)
  }
}
