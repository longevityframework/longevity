package longevity.integration.subdomain.foreignKeyOption

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class Associated(id: AssociatedId)
