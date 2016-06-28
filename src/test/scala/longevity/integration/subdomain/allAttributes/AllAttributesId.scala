package longevity.integration.subdomain.allAttributes

import longevity.subdomain.KeyVal

case class AllAttributesId(id: String)
extends KeyVal[AllAttributes](AllAttributes.keys.id)
