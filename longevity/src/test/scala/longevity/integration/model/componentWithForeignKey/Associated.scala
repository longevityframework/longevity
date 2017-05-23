package longevity.integration.model.componentWithForeignKey

import longevity.model.annotations.persistent

@persistent[DomainModel](keySet = Set(key(props.id)))
case class Associated(id: AssociatedId)
