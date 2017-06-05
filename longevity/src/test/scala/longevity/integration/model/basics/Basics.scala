package longevity.integration.model.basics

import org.joda.time.DateTime
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Basics(
  id: BasicsId,
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long,
  string: String,
  dateTime: DateTime)

object Basics {
  implicit val idKey = key(props.id)

  // for cassandra.BasicsQuerySpec:
  override val indexSet = Set(
    index(props.boolean),
    index(props.char),
    index(props.double),
    index(props.float),
    index(props.int),
    index(props.long),
    index(props.string),
    index(props.dateTime))
}
