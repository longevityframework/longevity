package longevity.integration.attributeLists

import longevity.subdomain._

case class AttributeLists(
  boolean: List[Boolean],
  char: List[Char],
  double: List[Double],
  float: List[Float],
  int: List[Int],
  long: List[Long],
  string: List[String])
extends RootEntity

object AttributeLists extends RootEntityType[AttributeLists]
