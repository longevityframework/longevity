package longevity.unit.model.ptype.indexSpec

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class IndexSampler(
  boolean: Boolean,
  char: Char,
  double: Double,
  float: Float,
  int: Int,
  long: Long)

object IndexSampler {
  override val indexSet = Set(
    index(props.boolean, props.char),
    index(props.boolean, props.char, props.double))
}
