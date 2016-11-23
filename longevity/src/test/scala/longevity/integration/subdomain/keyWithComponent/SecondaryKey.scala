package longevity.integration.subdomain.keyWithComponent

import longevity.subdomain.annotations.keyVal

@keyVal[KeyWithComponent]
case class SecondaryKey(id: String, component: Component)
