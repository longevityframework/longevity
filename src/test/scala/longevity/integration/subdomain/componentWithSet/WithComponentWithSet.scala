package longevity.integration.subdomain.componentWithSet

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class WithComponentWithSet(
  id: WithComponentWithSetId,
  component: Component)
extends Persistent

object WithComponentWithSet extends PType[WithComponentWithSet] {
  object props {
    val id = prop[WithComponentWithSetId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
