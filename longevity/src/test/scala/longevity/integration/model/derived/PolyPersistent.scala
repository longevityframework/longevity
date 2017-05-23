package longevity.integration.model.derived

import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel](keySet = Set(key(props.id)))
trait PolyPersistent {
  val id: PolyPersistentId
  val component: PolyComponent
}
