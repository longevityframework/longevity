package longevity.integration.subdomain.foreignKey

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
