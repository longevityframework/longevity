package longevity.integration.subdomain.componentWithList

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class WithComponentWithList(
  id: WithComponentWithListId,
  component: Component)
extends Root

object WithComponentWithList extends PType[WithComponentWithList] {
  object props {
    val id = prop[WithComponentWithListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
