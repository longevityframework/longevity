package longevity.integration.model.shorthandOptions

import longevity.model.annotations.persistent

@persistent[DomainModel]
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

object ShorthandOptions {
  implicit lazy val idKey = key(props.id)
}
