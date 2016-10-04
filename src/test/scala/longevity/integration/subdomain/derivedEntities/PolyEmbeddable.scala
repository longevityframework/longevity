package longevity.integration.subdomain.derived

import longevity.subdomain.Embeddable

trait PolyEmbeddable extends Embeddable {
  val id: PolyEmbeddableId
}
