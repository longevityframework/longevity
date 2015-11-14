package longevity.integration.subdomain.attributeLists

import com.github.nscala_time.time.Imports._
import longevity.subdomain._

case class AttributeLists(
  uri: String,
  boolean: List[Boolean],
  char: List[Char],
  double: List[Double],
  float: List[Float],
  int: List[Int],
  long: List[Long],
  string: List[String],
  dateTime: List[DateTime])
extends RootEntity

object AttributeLists extends RootEntityType[AttributeLists] {
  natKey("uri")
}

