package longevity.integration.subdomain.oneShorthand

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class OneShorthand(id: String, uri: Uri) extends Root

object OneShorthand extends RootType[OneShorthand] {
  object props {
    val id = prop[String]("id")
    val uri = prop[Uri]("uri")
  }
  object keys {
    val id = key(props.id)
  }
  object indexes {
    val uri = index(props.uri)
  }
}
