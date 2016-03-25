package longevity.integration.subdomain.withAssocOption

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class Associated(uri: String) extends Root

object Associated extends RootType[Associated] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
