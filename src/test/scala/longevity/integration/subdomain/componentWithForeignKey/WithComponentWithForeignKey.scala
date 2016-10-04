package longevity.integration.subdomain.componentWithForeignKey

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithComponentWithForeignKey(
  id: WithComponentWithForeignKeyId,
  component: ComponentWithForeignKey)
extends Root

object WithComponentWithForeignKey extends PType[WithComponentWithForeignKey] {
  object props {
    val id = prop[WithComponentWithForeignKeyId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
