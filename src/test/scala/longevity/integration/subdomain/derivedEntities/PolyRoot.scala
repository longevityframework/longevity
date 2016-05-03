package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.PolyPType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

trait PolyRoot extends Root {
  val uri: String
  val component: PolyEntity
}

object PolyRoot extends RootType[PolyRoot] with PolyPType[PolyRoot] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
