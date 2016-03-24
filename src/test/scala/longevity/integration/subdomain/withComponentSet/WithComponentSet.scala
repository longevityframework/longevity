package longevity.integration.subdomain.withComponentSet

import longevity.subdomain._

case class WithComponentSet(
  uri: String,
  components: Set[Component])
extends Root

object WithComponentSet extends RootType[WithComponentSet] {
  object props {
    val uri = prop[String]("uri")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
  }
}
