package longevity.integration.model.derived

import longevity.model.annotations.polyComponent

@polyComponent
trait PolyComponent {
  val id: PolyComponentId
}
