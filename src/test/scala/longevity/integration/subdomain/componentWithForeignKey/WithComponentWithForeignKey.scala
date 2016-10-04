package longevity.integration.subdomain.componentWithForeignKey

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentWithForeignKey(
  id: WithComponentWithForeignKeyId,
  component: ComponentWithForeignKey)
extends Persistent

object WithComponentWithForeignKey extends PType[WithComponentWithForeignKey] {
  object props {
    val id = prop[WithComponentWithForeignKeyId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
