package longevity.integration.subdomain.componentShorthands

import longevity.subdomain.PType

case class WithComponentWithShorthand(
  id: WithComponentWithShorthandId,
  component: ComponentWithShorthand)

object WithComponentWithShorthand extends PType[WithComponentWithShorthand] {
  object props {
    val id = prop[WithComponentWithShorthandId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
