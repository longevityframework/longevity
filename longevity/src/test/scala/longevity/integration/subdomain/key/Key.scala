package longevity.integration.subdomain.key

import longevity.model.annotations.persistent

@persistent(keySet = Set(key(props.id)))
case class Key(id: KeyId)
