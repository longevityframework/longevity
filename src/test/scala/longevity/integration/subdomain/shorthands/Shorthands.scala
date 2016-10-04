package longevity.integration.subdomain.shorthands

import longevity.ddd.subdomain.Root
import longevity.subdomain.PType

case class Shorthands(
  id: ShorthandsId,
  boolean: BooleanShorthand,
  char: CharShorthand,
  double: DoubleShorthand,
  float: FloatShorthand,
  int: IntShorthand,
  long: LongShorthand,
  string: StringShorthand,
  dateTime: DateTimeShorthand)
extends Root

object Shorthands extends PType[Shorthands] {
  object props {
    val id = prop[ShorthandsId]("id")
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
    val id = key[ShorthandsId](props.id)
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
