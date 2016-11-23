package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.polyComponent

@polyComponent
trait PolyEmbeddable {
  val id: PolyEmbeddableId
}
