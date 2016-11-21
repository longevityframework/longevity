package longevity.integration.subdomain.basicSets

import com.github.nscala_time.time.Imports._
import longevity.subdomain.PType
import longevity.subdomain.annotations.mprops

case class BasicSets(
  id: BasicSetsId,
  boolean: Set[Boolean],
  char: Set[Char],
  double: Set[Double],
  float: Set[Float],
  int: Set[Int],
  long: Set[Long],
  string: Set[String],
  dateTime: Set[DateTime])

@mprops object BasicSets extends PType[BasicSets] {
  object keys {
    val id = key(props.id)
  }
}
