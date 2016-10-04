package longevity.integration.subdomain.derived

import longevity.subdomain.DerivedPType

case class FirstDerivedPersistent(
  id: PolyPersistentId,
  first: String,
  component: PolyEmbeddable)
extends PolyPersistent

object FirstDerivedPersistent extends DerivedPType[FirstDerivedPersistent, PolyPersistent] {
  object props {
    val first = prop[String]("first")
    val componentId = prop[PolyEmbeddableId]("component.id")
  }
  object keys {
    val componentId = key(props.componentId)
  }
  object indexes {
    val first = index(props.first)
  }
}

