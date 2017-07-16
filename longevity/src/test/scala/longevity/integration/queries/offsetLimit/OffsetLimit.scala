package longevity.integration.queries.offsetLimit

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class OffsetLimit(i: Int, j: Int)

object OffsetLimit {
  override val indexSet = Set(index(props.i), index(props.j))
}
