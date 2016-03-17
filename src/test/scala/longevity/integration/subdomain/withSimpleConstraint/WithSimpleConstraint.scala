package longevity.integration.subdomain.withSimpleConstraint

import longevity.subdomain._

case class WithSimpleConstraint(
  id: String,
  primaryEmail: Email,
  emails: Set[Email])
extends Root

object WithSimpleConstraint extends RootType[WithSimpleConstraint] {
  val idProp = prop[String]("id")
  val idKey = key(idProp)
  val keySet = kscan(this)
  val indexSet = iscan(this)
}
