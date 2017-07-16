package longevity.unit.model.ptype.queryDsl

import longevity.model.KVType
import longevity.model.annotations.persistent

@persistent[DomainModel]
case class DslPersistent(path1: Int, path2: Double, path3: String, path4: AssociatedId)

case class AssociatedId(id: String)

object AssociatedId extends KVType[DomainModel, Associated, AssociatedId]

@persistent[DomainModel]
case class Associated(id: AssociatedId)

object Associated {
  implicit val idKey = key(props.id)
}
