package longevity.integration.subdomain.allShorthands

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

case class AllShorthands(
  uri: String,
  boolean: BooleanShorthand,
  char: CharShorthand,
  double: DoubleShorthand,
  float: FloatShorthand,
  int: IntShorthand,
  long: LongShorthand,
  string: StringShorthand,
  dateTime: DateTimeShorthand)
extends Root

object AllShorthands extends RootType[AllShorthands] {
  object props {
    val uri = prop[String]("uri")
    val boolean = prop[BooleanShorthand]("boolean")
    val char = prop[CharShorthand]("char")
    val double = prop[DoubleShorthand]("double")
    val float = prop[FloatShorthand]("float")
    val int = prop[IntShorthand]("int")
    val long = prop[LongShorthand]("long")
    val string = prop[StringShorthand]("string")
    val dateTime = prop[DateTimeShorthand]("dateTime")
  }
  object keys {
    val uri = key(props.uri)
  }
  object indexes {
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
