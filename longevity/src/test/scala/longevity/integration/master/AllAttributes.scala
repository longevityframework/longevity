package longevity.integration.master

import longevity.subdomain._

case class AllAttributes(
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String)
extends Entity

object AllAttributes extends EntityType[AllAttributes]
