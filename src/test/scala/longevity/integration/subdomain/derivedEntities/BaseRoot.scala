package longevity.integration.subdomain.derivedEntities

import longevity.subdomain.PolyType
import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

trait BaseRoot extends Root {
  val uri: String
  val component: BaseEntity
}

object BaseRoot extends RootType[BaseRoot] with PolyType[BaseRoot] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
