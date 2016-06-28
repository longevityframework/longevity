package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithSimpleConstraint(
  id: WithSimpleConstraintId,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object WithSimpleConstraint extends RootType[WithSimpleConstraint] {
  object props {
    val id = prop[WithSimpleConstraintId]("id")    
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
