package longevity.integration.subdomain.simpleConstraint

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class SimpleConstraint(
  id: SimpleConstraintId,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object SimpleConstraint extends RootType[SimpleConstraint] {
  object props {
    val id = prop[SimpleConstraintId]("id")    
  }
  object keys {
    val id = key(props.id)
  }
}
