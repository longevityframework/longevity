package longevity.integration.model.basicSets

import com.github.nscala_time.time.Imports._
import longevity.model.annotations.persistent

@persistent[DomainModel]
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

object BasicSets {
  implicit lazy val idKey = key(props.id)
}
