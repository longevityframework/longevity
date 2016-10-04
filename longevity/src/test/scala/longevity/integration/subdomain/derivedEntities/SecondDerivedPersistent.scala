package longevity.integration.subdomain.derived

import longevity.subdomain.DerivedPType

case class SecondDerivedPersistent(
  id: PolyPersistentId,
  second: String,
  component: PolyEmbeddable)
extends PolyPersistent

object SecondDerivedPersistent extends DerivedPType[SecondDerivedPersistent, PolyPersistent] {
  object props {
    val second = prop[String]("second")
  }
  object keys {
  }
  object indexes {
    val second = index(props.second)
  }
}

