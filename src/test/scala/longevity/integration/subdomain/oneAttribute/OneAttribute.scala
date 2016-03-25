package longevity.integration.subdomain.oneAttribute

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class OneAttribute(uri: String) extends Root

object OneAttribute extends RootType[OneAttribute] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
