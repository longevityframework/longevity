package longevity.integration.subdomain.withAssoc

import shorthands._
import longevity.subdomain._

case class WithAssoc(
  uri: String,
  associated: Assoc[Associated])
extends Root

object WithAssoc extends RootType[WithAssoc] {
  key(prop[String]("uri"))
  index(prop[String]("uri"))
  index(prop[Assoc[Associated]]("associated"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
