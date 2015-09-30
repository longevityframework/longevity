package longevity.integration.master

import longevity.subdomain._

case class OneShorthand(
  uri: String,
  string: StringShorthand)
extends RootEntity

object OneShorthand extends RootEntityType[OneShorthand] {
  natKey("uri")
}
