package longevity.integration.subdomain.foreignKeySet

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class Associated(id: AssociatedId) extends Root

object Associated extends RootType[Associated] {
  object props {
    val id = prop[AssociatedId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
