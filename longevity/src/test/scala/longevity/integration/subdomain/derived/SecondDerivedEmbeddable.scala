package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.derivedComponent

@derivedComponent[PolyEmbeddable]
case class SecondDerivedEmbeddable(
  id: PolyEmbeddableId,
  second: String)
extends PolyEmbeddable
