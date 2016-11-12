package longevity.integration.subdomain.keyWithForeignKey

import longevity.subdomain.PType

case class Associated(id: AssociatedId)

object Associated extends PType[Associated] {
  object props {
    val id = prop[AssociatedId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
