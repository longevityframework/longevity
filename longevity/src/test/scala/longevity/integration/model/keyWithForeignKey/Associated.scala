package longevity.integration.model.keyWithForeignKey

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class Associated(id: AssociatedId)
