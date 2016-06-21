package longevity.integration.subdomain.withSinglePropComponent

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class WithSinglePropComponent(id: String, uri: Uri) extends Root

object WithSinglePropComponent extends RootType[WithSinglePropComponent] {
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
