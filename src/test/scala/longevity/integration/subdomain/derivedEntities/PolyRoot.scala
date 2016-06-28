package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.ptype.PolyPType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

trait PolyRoot extends Root {
  val id: PolyRootId
  val component: PolyEntity
}

object PolyRoot extends RootType[PolyRoot] with PolyPType[PolyRoot] {
  object props {
    val id = prop[PolyRootId]("id")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
  }
}
