package longevity.integration.subdomain.keyInComponent

import longevity.subdomain.annotations.persistent

@persistent(keySet = Set(key(props.component.key)))
case class KeyInComponent(
  filler: String,
  component: Component)
