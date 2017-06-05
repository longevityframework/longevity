package longevity.integration.model.foreignKeyList

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Associated(id: AssociatedId)

object Associated {
  implicit val idKey = key(props.id)
}
