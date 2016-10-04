package longevity.integration.subdomain.shorthandLists

import longevity.ddd.subdomain.Root
import longevity.subdomain.ptype.RootType

case class ShorthandLists(
  id: ShorthandListsId,
  boolean: List[BooleanShorthand],
  char: List[CharShorthand],
  double: List[DoubleShorthand],
  float: List[FloatShorthand],
  int: List[IntShorthand],
  long: List[LongShorthand],
  string: List[StringShorthand],
  dateTime: List[DateTimeShorthand])
extends Root

object ShorthandLists extends RootType[ShorthandLists] {
  object props {
    val id = prop[ShorthandListsId]("id")
  }
  object keys {
    val id = key[ShorthandListsId](props.id)
  }
}
