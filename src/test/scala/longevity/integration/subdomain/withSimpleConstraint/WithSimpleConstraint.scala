package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain._

case class WithSimpleConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object WithSimpleConstraint extends RootType[WithSimpleConstraint] {
  object props {
    val id = prop[String]("id")    
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
