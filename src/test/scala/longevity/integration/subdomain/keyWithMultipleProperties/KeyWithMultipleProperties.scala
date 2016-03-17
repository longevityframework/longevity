package longevity.integration.subdomain.keyWithMultipleProperties

import longevity.subdomain._

case class KeyWithMultipleProperties(
  realm: String,
  name: String)
extends Root

object KeyWithMultipleProperties extends RootType[KeyWithMultipleProperties] {
  key(prop[String]("realm"), prop[String]("name"))
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
