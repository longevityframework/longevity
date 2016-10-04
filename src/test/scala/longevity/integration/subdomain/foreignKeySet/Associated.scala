package longevity.integration.subdomain.foreignKeySet

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class Associated(id: AssociatedId) extends Persistent

object Associated extends PType[Associated] {
  object props {
    val id = prop[AssociatedId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
