package longevity.integration.subdomain.withComponentOption

import longevity.subdomain._

case class WithComponentOption(
  uri: String,
  component: Option[Component])
extends Root

object WithComponentOption extends RootType[WithComponentOption] {
  key(prop[String]("uri"))
}

