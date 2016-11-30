package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.derivedComponent

@derivedComponent[PolyComponent]
case class FirstDerivedComponent(
  id: PolyComponentId,
  first: String)
extends PolyComponent
