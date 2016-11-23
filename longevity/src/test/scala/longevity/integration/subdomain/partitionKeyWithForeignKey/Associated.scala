package longevity.integration.subdomain.partitionKeyWithForeignKey

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class Associated(id: AssociatedId)
