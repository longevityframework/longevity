package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.Persistent
import longevity.subdomain.PType

case class ShorthandOptions(
  id: ShorthandOptionsId,
  boolean: Option[BooleanShorthand],
  char: Option[CharShorthand],
  double: Option[DoubleShorthand],
  float: Option[FloatShorthand],
  int: Option[IntShorthand],
  long: Option[LongShorthand],
  string: Option[StringShorthand],
  dateTime: Option[DateTimeShorthand])
extends Persistent

object ShorthandOptions extends PType[ShorthandOptions] {
  object props {
    val id = prop[ShorthandOptionsId]("id")
  }
  object keys {
    val id = key[ShorthandOptionsId](props.id)
  }
}
