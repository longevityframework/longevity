package longevity.integration.subdomain.withComponentWithAssoc

import longevity.subdomain._

case class WithComponentWithAssoc(
  uri: String,
  component: ComponentWithAssoc)
extends Root

object WithComponentWithAssoc extends RootType[WithComponentWithAssoc] {
  key(prop[String]("uri"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
