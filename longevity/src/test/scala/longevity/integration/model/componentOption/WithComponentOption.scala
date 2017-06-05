package longevity.integration.model.componentOption

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentOption(
  id: WithComponentOptionId,
  component: Option[Component])

object WithComponentOption {
  implicit val idKey = key(props.id)
}
