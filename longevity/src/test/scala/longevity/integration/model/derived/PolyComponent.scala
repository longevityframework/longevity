package longevity.integration.model.derived

import longevity.model.annotations.polyComponent

@polyComponent[DomainModel]
trait PolyComponent {
  val id: PolyComponentId
}
