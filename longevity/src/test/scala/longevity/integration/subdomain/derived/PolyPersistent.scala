package longevity.integration.subdomain.derived

import longevity.subdomain.annotations.polyPersistent

// TODO rename embeddable to component

@polyPersistent(keySet = Set(key(props.id)))
trait PolyPersistent {
  val id: PolyPersistentId
  val component: PolyEmbeddable
}
