package longevity.integration.subdomain.keyWithShorthand

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.id), key(props.secondaryKey)))
case class KeyWithShorthand(
  id: KeyWithShorthandId,
  secondaryKey: SecondaryKey)
