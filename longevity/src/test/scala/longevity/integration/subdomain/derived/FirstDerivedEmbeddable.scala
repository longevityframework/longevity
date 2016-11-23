package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.derivedComponent

@derivedComponent[PolyEmbeddable]
case class FirstDerivedEmbeddable(
  id: PolyEmbeddableId,
  first: String)
extends PolyEmbeddable
