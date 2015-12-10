package longevity.integration.subdomain.withAssoc

import longevity.subdomain._
import shorthands._

case class Associated(uri: String) extends RootEntity

object Associated extends RootEntityType[Associated] {
  key("uri")
}

