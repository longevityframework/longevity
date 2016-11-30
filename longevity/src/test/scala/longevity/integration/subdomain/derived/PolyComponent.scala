package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.polyComponent

@polyComponent
trait PolyComponent {
  val id: PolyComponentId
}
