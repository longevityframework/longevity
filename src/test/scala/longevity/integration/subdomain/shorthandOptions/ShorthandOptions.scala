package longevity.integration.subdomain.shorthandOptions

import longevity.subdomain.persistent.Root
import longevity.subdomain.ptype.RootType

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
extends Root

object ShorthandOptions extends RootType[ShorthandOptions] {
  object props {
    val id = prop[ShorthandOptionsId]("id")
  }
  object keys {
    val id = key[ShorthandOptionsId](props.id)
  }
}
