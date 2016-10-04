package longevity.integration.subdomain.derived

import longevity.subdomain.embeddable.Embeddable

trait PolyEmbeddable extends Embeddable {
  val id: PolyEmbeddableId
}
