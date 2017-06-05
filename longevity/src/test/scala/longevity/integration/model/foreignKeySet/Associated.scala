package longevity.integration.model.foreignKeySet

import longevity.model.annotations.persistent

@persistent[DomainModel]
case class Associated(id: AssociatedId)

object Associated {
  implicit lazy val idKey = key(props.id)
}
