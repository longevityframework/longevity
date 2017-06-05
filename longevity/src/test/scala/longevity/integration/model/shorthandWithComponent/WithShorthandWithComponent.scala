package longevity.integration.model.shorthandWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithShorthandWithComponent(
  id: WithShorthandWithComponentId,
  shorthandWithComponent: ShorthandWithComponent)

object WithShorthandWithComponent {
  implicit val idKey = key(props.id)
  override val indexSet = Set(index(props.shorthandWithComponent))
}
