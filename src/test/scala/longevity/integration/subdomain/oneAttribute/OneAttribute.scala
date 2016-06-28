package longevity.integration.subdomain.oneAttribute

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class OneAttribute(id: OneAttributeId) extends Root

object OneAttribute extends RootType[OneAttribute] {
  object props {
    val id = prop[OneAttributeId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
