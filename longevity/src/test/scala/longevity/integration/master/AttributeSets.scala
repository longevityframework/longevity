package longevity.integration.master

import longevity.subdomain._

case class AttributeSets(
  boolean: Set[Boolean],
  char: Set[Char],
  double: Set[Double],
  float: Set[Float],
  int: Set[Int],
  long: Set[Long],
  string: Set[String])
extends RootEntity

object AttributeSets extends RootEntityType[AttributeSets]
