package longevity.integration.subdomain.derived

import longevity.model.annotations.derivedPersistent

@derivedPersistent[PolyPersistent](
  keySet = Set(key(props.component.id)),
  indexSet = Set(index(props.first)))
case class FirstDerivedPersistent(
  id: PolyPersistentId,
  first: String,
  component: PolyComponent)
extends PolyPersistent
