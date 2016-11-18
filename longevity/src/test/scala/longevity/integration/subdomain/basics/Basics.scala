package longevity.integration.subdomain.basics

import org.joda.time.DateTime
import longevity.subdomain.PType
import longevity.subdomain.mprops

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

@mprops object Basics extends PType[Basics] {
  object keys {
    val id = key(props.id)
  }
  object indexes { // for cassandra.BasicsQuerySpec
    val boolean = index(props.boolean)
    val char = index(props.char)
    val double = index(props.double)
    val float = index(props.float)
    val int = index(props.int)
    val long = index(props.long)
    val string = index(props.string)
    val dateTime = index(props.dateTime)
  }
}
