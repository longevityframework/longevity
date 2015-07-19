package longevity.integration.master

import longevity.subdomain._

case class AttributeOptions(
  boolean: Option[Boolean],
  char: Option[Char],
  double: Option[Double],
  float: Option[Float],
  int: Option[Int],
  long: Option[Long],
  string: Option[String])
extends RootEntity

object AttributeOptions extends RootEntityType[AttributeOptions]
