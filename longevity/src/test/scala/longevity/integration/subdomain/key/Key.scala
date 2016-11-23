package longevity.integration.subdomain.key

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class Key(id: KeyId)
