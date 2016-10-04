package longevity.integration.subdomain.componentWithList

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentWithList(
  id: WithComponentWithListId,
  component: Component)
extends Persistent

object WithComponentWithList extends PType[WithComponentWithList] {
  object props {
    val id = prop[WithComponentWithListId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
