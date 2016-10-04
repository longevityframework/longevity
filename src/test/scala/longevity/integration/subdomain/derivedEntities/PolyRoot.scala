package longevity.integration.subdomain.derived

import longevity.subdomain.PolyPType
import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

trait PolyRoot extends Root {
  val id: PolyRootId
  val component: PolyEmbeddable
}

object PolyRoot extends PType[PolyRoot] with PolyPType[PolyRoot] {
  object props {
    val id = prop[PolyRootId]("id")
  }
  object keys {
    val id = key(props.id)
  }
}
