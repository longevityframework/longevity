package longevity.integration.model.componentSet

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class WithComponentSet(
  id: WithComponentSetId,
  components: Set[Component])

object WithComponentSet {
  implicit val idKey = key(props.id)
}
