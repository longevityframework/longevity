package longevity.integration.subdomain.derived

import longevity.subdomain.ptype.PolyPType
import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

trait PolyRoot extends Root {
  val id: PolyRootId
  val component: PolyEmbeddable
}

object PolyRoot extends RootType[PolyRoot] with PolyPType[PolyRoot] {
  object props {
    val id = prop[PolyRootId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
