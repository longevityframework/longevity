package longevity.integration.subdomain.withComponent

import longevity.subdomain._

case class WithComponent(
  uri: String,
  component: Component)
extends Root

object WithComponent extends RootType[WithComponent] {
  key(prop[String]("uri"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
