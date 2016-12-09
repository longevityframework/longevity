package longevity.integration.model.shorthands

import longevity.model.annotations.persistent

@persistent(
  keySet = Set(key(props.id)),
  indexSet = Set(
    index(props.boolean),
    index(props.char),
    index(props.double),
    index(props.float),
    index(props.int),
    index(props.long),
    index(props.string),
    index(props.dateTime)))
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
