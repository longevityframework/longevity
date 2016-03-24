package longevity.integration.subdomain.withComponent

import longevity.subdomain._

case class WithComponent(
  uri: String,
  component: Component)
extends Root

object WithComponent extends RootType[WithComponent] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
