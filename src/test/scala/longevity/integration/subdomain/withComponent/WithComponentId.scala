package longevity.integration.subdomain.withComponent

import longevity.subdomain.KeyVal

case class WithComponentId(id: String)
extends KeyVal[WithComponent](WithComponent.keys.id)
