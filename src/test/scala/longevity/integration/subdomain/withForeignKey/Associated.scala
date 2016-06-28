package longevity.integration.subdomain.withForeignKey

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Associated(id: AssociatedId) extends Root

object Associated extends RootType[Associated] {
  object props {
    val id = prop[AssociatedId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
