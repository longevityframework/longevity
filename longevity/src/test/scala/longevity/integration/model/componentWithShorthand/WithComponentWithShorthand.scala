package longevity.integration.model.componentShorthands

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentWithShorthand(
  id: WithComponentWithShorthandId,
  component: ComponentWithShorthand)

object WithComponentWithShorthand {
  implicit lazy val idKey = key(props.id)
}
