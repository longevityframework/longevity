package longevity.integration.model.derived

import longevity.model.annotations.polyPersistent

@polyPersistent[DomainModel]
trait PolyPersistent {
  val id: PolyPersistentId
  val component: PolyComponent
}

object PolyPersistent {
  implicit lazy val idKey = key(props.id)
}
