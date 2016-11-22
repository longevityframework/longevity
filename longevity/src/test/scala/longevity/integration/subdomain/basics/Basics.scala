package longevity.integration.subdomain.basics

import org.joda.time.DateTime
import longevity.subdomain.annotations.persistent

@persistent(
  keySet = Set(key(Basics.props.id)),
  indexSet = Set( // for cassandra.BasicsQuerySpec
    index(props.boolean),
    index(props.char),
    index(props.double),
    index(props.float),
    index(props.int),
    index(props.long),
    index(props.string),
    index(props.dateTime)))
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
