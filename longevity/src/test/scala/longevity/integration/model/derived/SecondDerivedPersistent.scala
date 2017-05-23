package longevity.integration.model.derived

import longevity.model.annotations.derivedPersistent

@derivedPersistent[DomainModel, PolyPersistent](
  keySet = emptyKeySet,
  indexSet = Set(index(props.second)))
case class SecondDerivedPersistent(
  id: PolyPersistentId,
  second: String,
  component: PolyComponent)
extends PolyPersistent
