package longevity.integration.subdomain.withAssoc

import shorthands._
import longevity.subdomain._

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Root

object WithAssoc extends RootType[WithAssoc] {
  key("uri")
  index("uri")
  index("associated")
}

