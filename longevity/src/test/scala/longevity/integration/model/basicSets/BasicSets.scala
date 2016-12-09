package longevity.integration.model.basicSets

import com.github.nscala_time.time.Imports._
import longevity.model.annotations.persistent

@persistent(keySet = Set(key(BasicSets.props.id)))
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
