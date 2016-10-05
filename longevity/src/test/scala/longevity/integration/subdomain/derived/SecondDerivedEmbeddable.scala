package longevity.integration.subdomain.derived

case class SecondDerivedEmbeddable(
  id: PolyEmbeddableId,
  second: String)
extends PolyEmbeddable
