package longevity.integration.subdomain.keyWithComponent

import longevity.subdomain.KeyVal

case class SecondaryKey(
  id: String,
  component: Component)
extends KeyVal[KeyWithComponent, SecondaryKey](
  KeyWithComponent.keys.secondaryKey)
