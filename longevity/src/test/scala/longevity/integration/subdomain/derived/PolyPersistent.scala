package longevity.integration.subdomain.derived

import longevity.subdomain.PolyPType
import longevity.subdomain.PType

trait PolyPersistent {
  val id: PolyPersistentId
  val component: PolyEmbeddable
}

object PolyPersistent extends PType[PolyPersistent] with PolyPType[PolyPersistent] {
  object props {
    val id = prop[PolyPersistentId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
