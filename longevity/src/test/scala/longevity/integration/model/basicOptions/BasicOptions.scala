package longevity.integration.model.basicOptions

import com.github.nscala_time.time.Imports._
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class BasicOptions(
  id: BasicOptionsId,
  boolean: Option[Boolean],
  char: Option[Char],
  double: Option[Double],
  float: Option[Float],
  int: Option[Int],
  long: Option[Long],
  string: Option[String],
  dateTime: Option[DateTime])

object BasicOptions {
  implicit val idKey = key(props.id)
}
