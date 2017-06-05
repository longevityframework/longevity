package longevity.integration.model.shorthandWithComponent

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithShorthandWithComponent(
  id: WithShorthandWithComponentId,
  shorthandWithComponent: ShorthandWithComponent)

object WithShorthandWithComponent {

  implicit lazy val idKey = key(props.id)

  override implicit lazy val indexSet = Set(index(props.shorthandWithComponent))

}
