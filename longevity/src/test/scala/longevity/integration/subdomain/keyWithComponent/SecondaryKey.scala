package longevity.integration.subdomain.keyWithComponent

import longevity.model.annotations.keyVal

@keyVal[KeyWithComponent]
case class SecondaryKey(id: String, component: Component)
